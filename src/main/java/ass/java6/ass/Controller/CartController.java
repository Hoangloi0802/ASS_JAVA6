package ass.java6.ass.Controller;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Order;
import ass.java6.ass.Entity.OrderDetail;
import ass.java6.ass.Entity.Product;
import ass.java6.ass.Repository.ProductRepository;
import ass.java6.ass.Service.CartService;
import ass.java6.ass.Service.AccoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@ControllerAdvice
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccoutService accoutService;
    private Account getAuthenticatedAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return accoutService.findByUsername(authentication.getName()).orElse(null);
    }
    // 🛒 QUẢN LÝ GIỎ HÀNG BẰNG GET
    @GetMapping("/giohang")
    public String manageCart(@RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "quantity", required = false, defaultValue = "1") int quantity,

            Model model, RedirectAttributes redirectAttributes) {

        Account account = getAuthenticatedAccount();
        if (account == null) {
            return "redirect:/dangnhap"; // Nếu chưa đăng nhập, chuyển hướng
        }

        Product product = (productId != null) ? productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại")) : null;

        if ("add".equals(action) && product != null) {
            cartService.addToCart(account, product, quantity);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã thêm vào giỏ hàng!");
            return "redirect:/product/" + product.getId();
        }

        if ("update".equals(action) && product != null) {
            cartService.updateQuantity(account, product, quantity);
            redirectAttributes.addFlashAttribute("message", "Giỏ hàng đã được cập nhật!");
            return "redirect:/giohang";
        }

        if ("remove".equals(action) && product != null) {
            cartService.removeFromCart(account, product);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã bị xóa!");
            return "redirect:/giohang";
        }

        // Hiển thị giỏ hàng
        Order cart = cartService.getCurrentCart(account);
        List<OrderDetail> cartItems = cart.getOrderDetails();
        double totalPrice = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "home/giohang";
    }

    @ModelAttribute
    public void addCartCountToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("cartCount", 0);
            return;
        }

        String username = authentication.getName();
        Account account = accoutService.findByUsername(username).orElse(null);
        if (account == null) {
            model.addAttribute("cartCount", 0);
            return;
        }

        // Lấy giỏ hàng của người dùng
        int cartCount = cartService.getTotalItemsInCart(account); // 👈 Tính tổng số lượng sản phẩm
        model.addAttribute("cartCount", cartCount);

    }

 
}
