package ass.java6.ass.Controller;

import ass.java6.ass.Entity.OrderDetail;
import ass.java6.ass.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/giohang")
public class CartController {

    @Autowired
    private CartService cartService;

    // 🛒 Xem giỏ hàng
    @GetMapping
    public String viewCart(Model model) {
        String username = getAuthenticatedUsername();
        List<OrderDetail> cartItems = cartService.getCartItems(username);

        // Nếu giỏ hàng trống, truyền danh sách rỗng để tránh lỗi
        model.addAttribute("cartItems", cartItems != null ? cartItems : new ArrayList<>());

        // Tính tổng tiền
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        model.addAttribute("totalPrice", totalPrice);

        return "home/giohang";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity) {
        String username = getAuthenticatedUsername(); // ✅ Lấy username từ Spring Security
        cartService.addToCart(username, productId, quantity);
        return "redirect:/giohang";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("productId") Long productId,
                                 @RequestParam("change") int change) {
        String username = getAuthenticatedUsername();
    
        // Lấy sản phẩm trong giỏ hàng
        Optional<OrderDetail> orderDetail = cartService.findItemInCart(username, productId);
    
        // Nếu sản phẩm có trong giỏ hàng
        if (orderDetail.isPresent()) {
            OrderDetail item = orderDetail.get();
            int newQuantity = item.getQuantity() + change;
    
            // Nếu số lượng mới >= 1 thì cập nhật, ngược lại không làm gì
            if (newQuantity >= 1) {
                cartService.updateQuantity(username, productId, newQuantity);
            }
        }
        
        return "redirect:/giohang"; // Reload lại trang giỏ hàng
    }
    

    // 🛒 Xóa sản phẩm khỏi giỏ hàng
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("productId") Long productId) {
        String username = getAuthenticatedUsername();
        cartService.removeFromCart(username, productId);
        return "redirect:/giohang";
    }

    // 🛒 Xóa toàn bộ giỏ hàng
    @PostMapping("/clear")
    public String clearCart() {
        String username = getAuthenticatedUsername(); // ✅ Lấy username từ Spring Security
        cartService.clearCart(username);
        return "redirect:/giohang";
    }

    // ✅ HÀM LẤY USERNAME TỪ SPRING SECURITY
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName(); // Lấy username của người dùng hiện tại
        }
        throw new RuntimeException("User not authenticated");
    }

}
