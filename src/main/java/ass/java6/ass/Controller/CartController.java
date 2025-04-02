package ass.java6.ass.Controller;

import ass.java6.ass.Entity.*;
import ass.java6.ass.Repository.ProductRepository;
import ass.java6.ass.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ControllerAdvice
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private VoucherService voucherService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AccoutService accountService;

    private static final String LOGIN_REDIRECT = "redirect:/dangnhap";
    private static final String CART_VIEW = "home/giohang";

    private Account getAuthenticatedAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? accountService.findByUsername(auth.getName()).orElse(null)
                : null;
    }

    @GetMapping
    public String manageCart(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Model model,
            RedirectAttributes redirectAttributes) {

        Account account = getAuthenticatedAccount();
        if (account == null)
            return LOGIN_REDIRECT;

        try {
            Product product = (productId != null)
                    ? productRepository.findById(productId)
                            .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"))
                    : null;

            switch (String.valueOf(action)) {
                case "add":
                    if (product != null) {
                        cartService.addToCart(account, product, quantity);
                        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã thêm vào giỏ hàng!");
                        return "redirect:/product/" + product.getId();
                    }
                    break;
                case "update":
                    if (product != null) {
                        cartService.updateQuantity(account, product, quantity);
                        redirectAttributes.addFlashAttribute("message", "Giỏ hàng đã được cập nhật!");
                        return "redirect:/cart";
                    }
                    break;
                case "remove":
                    if (product != null) {
                        cartService.removeFromCart(account, product);
                        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã bị xóa!");
                        return "redirect:/cart";
                    }
                    break;
            }

            Order cart = cartService.getCurrentCart(account);
            populateCartModel(cart, model);
            return CART_VIEW;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @ModelAttribute
    public void addCartCount(Model model) {
        Account account = getAuthenticatedAccount();
        int cartCount = (account != null) ? cartService.getTotalItemsInCart(account) : 0;
        model.addAttribute("cartCount", cartCount);
    }

    @GetMapping("/update-cart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(
            @RequestParam Long productId,
            @RequestParam int quantity) {

        Account account = getAuthenticatedAccount();
        if (account == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
            cartService.updateQuantity(account, product, quantity);
            Order cart = cartService.getCurrentCart(account);
            validateVoucher(cart); // Kiểm tra và xóa voucher nếu không hợp lệ
            return ResponseEntity.ok(createCartResponse(account));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/apply-voucher")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> applyVoucher(@RequestParam String voucherCode) {
        Account account = getAuthenticatedAccount();
        if (account == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Order cart = cartService.getCurrentCart(account);
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            if (cart != null && cart.getVoucher() != null) {
                cart.setVoucher(null);
                cartService.saveCart(cart);
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Giỏ hàng của bạn đang trống."));
        }

        Voucher voucher = voucherService.findByCode(voucherCode);
        if (voucher == null) {
            cart.setVoucher(null);
            cartService.saveCart(cart);
            return ResponseEntity.badRequest().body(Map.of("error", "Voucher không hợp lệ."));
        }

        double subtotal = cartService.calculateSubtotal(account);
        if (subtotal < voucher.getMinOrderValue()) {
            cart.setVoucher(null);
            cartService.saveCart(cart);
            return ResponseEntity.badRequest().body(Map.of("error", "Đơn hàng chưa đủ điều kiện áp dụng voucher."));
        }

        // ✅ Kiểm tra xem giỏ hàng có sản phẩm thuộc danh mục của voucher không
        boolean hasValidProduct = cart.getOrderDetails().stream()
                .anyMatch(orderDetail -> orderDetail.getProduct().getCategory().getId()
                        .equals(voucher.getCategory().getId()));

        if (!hasValidProduct) {
            cart.setVoucher(null);
            cartService.saveCart(cart);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Voucher không áp dụng cho sản phẩm trong giỏ hàng."));
        }

        cart.setVoucher(voucher);
        cartService.saveCart(cart);
        return ResponseEntity.ok(createCartResponse(account));
    }

    @GetMapping("/checkout")
    public String checkout(RedirectAttributes redirectAttributes) {
        Account account = getAuthenticatedAccount();
        if (account == null)
            return "redirect:/dangnhap";

        Order cart = cartService.getCurrentCart(account);
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng của bạn đang trống!");
            return "redirect:/cart";
        }

        return "redirect:/thanhtoan"; // Chuyển sang trang thanh toán
    }

    // Helper methods
    private void populateCartModel(Order cart, Model model) {
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            if (cart != null && cart.getVoucher() != null) {
                cart.setVoucher(null);
                cartService.saveCart(cart);
            }
            model.addAttribute("subtotal", 0.0);
            model.addAttribute("discount", 0.0);
            model.addAttribute("totalAmount", 0.0);
            model.addAttribute("vouchers", Collections.emptyList());
            model.addAttribute("cartItems", Collections.emptyList());
            model.addAttribute("totalPrice", 0.0);
        } else {
            validateVoucher(cart); // Kiểm tra và xóa voucher nếu không hợp lệ
            List<OrderDetail> cartItems = cart.getOrderDetails();
            double subtotal = cartService.calculateSubtotal(cart.getAccount());
            double discount = cartService.calculateDiscount(cart.getAccount());
            double total = cartService.calculateTotalPrice(cart.getAccount());
            double totalPrice = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

            model.addAttribute("subtotal", subtotal);
            model.addAttribute("discount", discount);
            model.addAttribute("totalAmount", total);
            model.addAttribute("vouchers", voucherService.getValidVouchers(subtotal));
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", totalPrice);
        }
    }

    private void validateVoucher(Order cart) {
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            if (cart != null && cart.getVoucher() != null) {
                cart.setVoucher(null);
                cartService.saveCart(cart);
            }
            return;
        }

        Voucher voucher = cart.getVoucher();
        if (voucher == null)
            return;

        double subtotal = cartService.calculateSubtotal(cart.getAccount());
        if (subtotal < voucher.getMinOrderValue()) {
            cart.setVoucher(null);
            cartService.saveCart(cart);
        }
    }

    private Map<String, Object> createCartResponse(Account account) {
        Map<String, Object> response = new HashMap<>();
        response.put("subtotal", cartService.calculateSubtotal(account));
        response.put("discount", cartService.calculateDiscount(account));
        response.put("total", cartService.calculateTotalPrice(account));
        return response;
    }
}