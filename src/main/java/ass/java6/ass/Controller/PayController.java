package ass.java6.ass.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Order;
import ass.java6.ass.Service.AccoutService;
import ass.java6.ass.Service.CartService;
import ass.java6.ass.Service.MomoService;
import ass.java6.ass.Service.MomoService.PaymentResponse;

@Controller
public class PayController {
    @Autowired
    private CartService cartService;

    @Autowired
    private AccoutService accountService;
    @Autowired
    private MomoService momoService;

    @GetMapping("/thanhtoan")
    public String thanhtoan(Model model) {
        Account account = getAuthenticatedAccount();
        if (account == null) {
            return "redirect:/dangnhap";
        }

        Order cart = cartService.getCurrentCart(account);
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            model.addAttribute("cartItems", Collections.emptyList());
            model.addAttribute("subtotal", 0.0);
            model.addAttribute("discount", 0.0);
            model.addAttribute("totalAmount", 0.0);
        } else {
            model.addAttribute("cartItems", cart.getOrderDetails());
            model.addAttribute("subtotal", cartService.calculateSubtotal(account));
            model.addAttribute("discount", cartService.calculateDiscount(account));
            model.addAttribute("totalAmount", cartService.tongthanhtoan(account));
        }
        model.addAttribute("usedVoucherCode", cartService.getUsedVoucherCode(account));

        model.addAttribute("account", account);
        return "home/thanhtoan";
    }

    @PostMapping("/thanhtoan/dathang")
    public String placeOrder(@RequestParam("address") String address, RedirectAttributes redirectAttributes) {
        Account account = getAuthenticatedAccount();
        if (account == null) {
            return "redirect:/dangnhap";
        }

        try {
            Order order = cartService.createOrderFromCart(account, address);
            cartService.clearCart(account); // Xóa sản phẩm từ giỏ hàng

            // Chuyển hướng kèm theo orderId
            return "redirect:/thanhtoan/thanhcong?orderId=" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi đặt hàng: " + e.getMessage());
            return "redirect:/thanhtoan";
        }
    }

    @GetMapping("/thanhtoan/momo")
public ResponseEntity<?> thanhToanMoMo(@RequestParam("amount") String amount) {
    try {
        PaymentResponse response = momoService.createPaymentRequest(amount);

        if (response.getResultCode() == 0) { // Thành công
            return ResponseEntity.ok(Collections.singletonMap("payUrl", response.getPayUrl()));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Lỗi MoMo: " + response.getMessage()));
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(Collections.singletonMap("error", "Lỗi hệ thống: " + e.getMessage()));
    }
}
// @PostMapping("/thanhtoan/momo/ipn")
// public ResponseEntity<?> momoIpn(@RequestBody Map<String, Object> payload) {
//     try {
//         String resultCode = String.valueOf(payload.get("resultCode"));
//         String orderId = (String) payload.get("orderId");
        
//         if ("0".equals(resultCode)) {
//             // ✅ Thanh toán thành công
          
//             return ResponseEntity.ok(Collections.singletonMap("message", "success"));
//         } else {
//             // ❌ Thanh toán thất bại
//             return ResponseEntity.ok(Collections.singletonMap("message", "failed"));
//         }
//     } catch (Exception e) {
//         return ResponseEntity.status(500).body(Collections.singletonMap("message", "error"));
//     }
// }

    

    @GetMapping("/thanhtoan/thanhcong")
    public String orderSuccess(@RequestParam(value = "orderId", required = false) Long orderId, Model model) {
        if (orderId == null) {
            return "redirect:/";
        }
        Order order = cartService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/";
        }

        double subtotal = order.getOrderDetails().stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();

        double discount = (order.getVoucher() != null) ? order.getVoucher().getDiscountAmount() : 0.0;
        double shippingFee = 50000;
        double totalAmount = subtotal - discount + shippingFee;

        model.addAttribute("order", order);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("totalAmount", totalAmount);

        return "home/donmua";
    }

    @GetMapping("/donhang")
    public String listOrders(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Lấy username của người dùng đăng nhập

        // Lấy danh sách đơn hàng của user
        List<Order> orders = cartService.getOrdersByUsername(username);

        model.addAttribute("orders", orders);

        return "home/donhang"; // Trả về trang danh sách đơn hàng
    }

    @GetMapping("/chitietdonhang/{orderId}")
    public String viewOrder(@PathVariable("orderId") Long orderId, Model model) {
        Order order = cartService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/";
        }
        model.addAttribute("order", order);
        return "home/chitietdonhang";
    }

    private Account getAuthenticatedAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? accountService.findByUsername(auth.getName()).orElse(null)
                : null;
    }
}