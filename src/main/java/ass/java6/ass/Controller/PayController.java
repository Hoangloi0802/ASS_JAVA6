// package ass.java6.ass.Controller;

// import ass.java6.ass.Config.VNPayConfig;
// import ass.java6.ass.Entity.Account;
// import ass.java6.ass.Entity.Order;
// import ass.java6.ass.Service.AccoutService;
// import ass.java6.ass.Service.CartService;
// import ass.java6.ass.Service.VNPayService;

// import java.util.Collections;
// import java.util.List;

// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;
// import jakarta.servlet.http.HttpServletRequest;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import ass.java6.ass.Entity.Account;
// import ass.java6.ass.Entity.Order;
// import ass.java6.ass.Service.AccoutService;
// import ass.java6.ass.Service.CartService;
// import ass.java6.ass.Service.MomoService;

// @Controller
// public class PayController {

//     @Autowired
//     private CartService cartService;

//     @Autowired
//     private AccoutService accountService;

//     @Autowired
//     private VNPayService vnPayService;
//     @Autowired
//     private MomoService momoService;

//     @GetMapping("/thanhtoan")
//     public String thanhtoan(Model model) {
//         Account account = getAuthenticatedAccount();
//         if (account == null) {
//             return "redirect:/dangnhap";
//         }

//         Order cart = cartService.getCurrentCart(account);
//         if (cart == null || cart.getOrderDetails().isEmpty()) {
//             model.addAttribute("cartItems", Collections.emptyList());
//             model.addAttribute("subtotal", 0.0);
//             model.addAttribute("discount", 0.0);
//             model.addAttribute("totalAmount", 0.0);
//         } else {
//             model.addAttribute("cartItems", cart.getOrderDetails());
//             model.addAttribute("subtotal", cartService.calculateSubtotal(account));
//             model.addAttribute("discount", cartService.calculateDiscount(account));
//             model.addAttribute("totalAmount", cartService.tongthanhtoan(account));
//         }
//         model.addAttribute("usedVoucherCode", cartService.getUsedVoucherCode(account));
//         model.addAttribute("account", account);
//         return "home/thanhtoan";
//     }

//     @PostMapping("/thanhtoan/dathang")
//     public String placeOrder(
//             @RequestParam("address") String address,
//             @RequestParam("paymentMethod") String paymentMethod,
//             HttpServletRequest request,
//             RedirectAttributes redirectAttributes,
//             Model model) {
//         Account account = getAuthenticatedAccount();
//         if (account == null) {
//             return "redirect:/dangnhap";
//         }

//         try {
//             if ("vnpay".equalsIgnoreCase(paymentMethod)) {
//                 long amount = (long) cartService.calculateTotalPrice(account);
//                 if (amount < 5000) {
//                     redirectAttributes.addFlashAttribute("error", "Số tiền phải lớn hơn 5,000 VND");
//                     return "redirect:/thanhtoan";
//                 }
//                 if (amount >= 1_000_000_000) {
//                     redirectAttributes.addFlashAttribute("error", "Số tiền phải nhỏ hơn 1 tỷ VND");
//                     return "redirect:/thanhtoan";
//                 }
//                 Order tempOrder = cartService.createTemporaryOrder(account, address);
//                 request.getSession().setAttribute("tempOrder", tempOrder);
//                 model.addAttribute("orderId", "ORDER_" + System.currentTimeMillis());
//                 model.addAttribute("amount", amount);
//                 return "pay"; // Chuyển hướng đến trang xác nhận
//             } else {
//                 // Thanh toán COD hoặc phương thức khác
//                 Order order = cartService.createOrderFromCart(account, address); // Lưu đơn hàng
//                 return "redirect:/thanhtoan/thanhcong?orderId=" + order.getId();
//             }
//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", "Lỗi khi đặt hàng: " + e.getMessage());
//             return "redirect:/thanhtoan";
//         }
//     }

//     @PostMapping("/vnpay/create-payment")
//     public String createPayment(
//             @RequestParam("amount") long amount,
//             @RequestParam("orderId") String orderId,
//             HttpServletRequest request,
//             RedirectAttributes redirectAttributes) {
//         try {
//             Order tempOrder = (Order) request.getSession().getAttribute("tempOrder");
//             if (tempOrder == null) {
//                 redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng tạm thời!");
//                 return "redirect:/thanhtoan";
//             }
//             cartService.saveOrder(tempOrder); // Không cần setStatus vì createTemporaryOrder đã làm
//             String paymentUrl = vnPayService.createPaymentUrl(request, amount, orderId);
//             request.getSession().removeAttribute("tempOrder");
//             return "redirect:" + paymentUrl;
//         } catch (IllegalArgumentException e) {
//             redirectAttributes.addFlashAttribute("error", e.getMessage());
//             return "redirect:/thanhtoan";
//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo URL thanh toán: " + e.getMessage());
//             return "redirect:/thanhtoan";
//         }
//     }

//     @GetMapping("/thanhtoan/thanhcong")
//     public String orderSuccess(@RequestParam(value = "orderId", required = false) String orderId, Model model) {
//         if (orderId == null) {
//             return "redirect:/";
//         }

//         Long orderIdLong;
//         try {
//             orderIdLong = Long.parseLong(orderId.replace("ORDER_", ""));
//         } catch (NumberFormatException e) {
//             return "redirect:/";
//         }

//         Order order = cartService.getOrderById(orderIdLong);
//         if (order == null) {
//             return "redirect:/";
//         }


//         model.addAttribute("order", order);
//         double subtotal = order.getOrderDetails().stream()
//                 .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
//                 .sum();
//         double shippingFee = 50000;
//         double totalAmount = subtotal + shippingFee;

//         double subtotal = order.getOrderDetails().stream()
//                 .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
//                 .sum();
//         double discount = (order.getVoucher() != null) ? order.getVoucher().getDiscountAmount() : 0.0;
//         double shippingFee = 50000;
//         double totalAmount = subtotal - discount + shippingFee;

//         model.addAttribute("order", order);
//         model.addAttribute("subtotal", subtotal);
//         model.addAttribute("discount", discount);
//         model.addAttribute("shippingFee", shippingFee);
//         model.addAttribute("totalAmount", totalAmount);

//         return "home/donmua";
//     }

//     @GetMapping("/api/vnpay/callback")
//     public String handleVNPayCallback(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
//         if (request == null || request.getParameterMap() == null) {
//             redirectAttributes.addFlashAttribute("message", "Yêu cầu không hợp lệ!");
//             return "redirect:/thanhtoan/thanhcong?orderId=" + request.getParameter("vnp_TxnRef").replace("ORDER_", "");
//         }

//         Map<String, String> params = new HashMap<>();
//         for (String key : request.getParameterMap().keySet()) {
//             String[] values = request.getParameterValues(key);
//             if (values != null && values.length > 0) {
//                 params.put(key, values[0]);
//             }
//         }

//         String vnp_SecureHash = params.get("vnp_SecureHash");
//         if (vnp_SecureHash == null) {
//             redirectAttributes.addFlashAttribute("message", "Không tìm thấy chữ ký!");
//             return "redirect:/thanhtoan/thanhcong?orderId=" + params.get("vnp_TxnRef").replace("ORDER_", "");
//         }

//         params.remove("vnp_SecureHash");
//         params.remove("vnp_SecureHashType");

//         try {
//             String signValue = vnPayService.hmacSHA512(VNPayConfig.VNP_HASHSECRET, buildQueryString(params));
//             if (signValue.equals(vnp_SecureHash)) {
//                 String vnp_ResponseCode = params.get("vnp_ResponseCode");
//                 String orderId = params.get("vnp_TxnRef").replace("ORDER_", "");
//                 Order order = cartService.getOrderById(Long.parseLong(orderId));
//                 if (order != null) {
//                     if ("00".equals(vnp_ResponseCode)) {
//                         System.out.println("Before update - Order ID: " + order.getId() + ", Status: " + order.getStatus());
//                         order.setStatus("SHIPPING");
//                         cartService.updateOrder(order);
//                         cartService.clearCart(getAuthenticatedAccount()); // Xóa giỏ hàng sau khi thành công
//                         redirectAttributes.addFlashAttribute("message",
//                                 "Thanh toán thành công! Mã giao dịch: " + orderId);
//                     } else {
//                         order.setStatus("FAILED");
//                         cartService.updateOrder(order);
//                         redirectAttributes.addFlashAttribute("message",
//                                 "Thanh toán thất bại! Mã lỗi: " + vnp_ResponseCode);
//                     }
//                 } else {
//                     redirectAttributes.addFlashAttribute("message", "Không tìm thấy đơn hàng!");
//                 }
//             } else {
//                 redirectAttributes.addFlashAttribute("message", "Chữ ký không hợp lệ!");
//             }
//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("message", "Lỗi khi xử lý callback: " + e.getMessage());
//         }

//         return "redirect:/thanhtoan/thanhcong?orderId=" + params.get("vnp_TxnRef").replace("ORDER_", "");
//     }

//     @GetMapping("/donhang")
//     public String listOrders(Model model) {
//         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//         String username = auth.getName();
//         List<Order> orders = cartService.getOrdersByUsername(username);
//         model.addAttribute("orders", orders);
//         return "home/donhang";
//     }

//     @GetMapping("/chitietdonhang/{orderId}")
//     public String viewOrder(@PathVariable("orderId") Long orderId, Model model) {
//         Order order = cartService.getOrderById(orderId);
//         if (order == null) {
//             return "redirect:/";
//         }
//         model.addAttribute("order", order);
//         return "home/chitietdonhang";
//     }
//         // Xử lý hành động "Đã nhận hàng"
//         @PostMapping("/chitietdonhang/{id}/confirm-received")
//         public String confirmReceived(@PathVariable("id") Long id) {
//             cartService.confirmOrderReceived(id);
//             return "redirect:/chitietdonhang/" + id;
//         }
//     private Account getAuthenticatedAccount() {
//         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//         return (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
//                 ? accountService.findByUsername(auth.getName()).orElse(null)
//                 : null;
//     }

//     private String buildQueryString(Map<String, String> params) {
//         List<String> fieldNames = new ArrayList<>(params.keySet());
//         Collections.sort(fieldNames);
//         StringBuilder query = new StringBuilder();
//         for (String fieldName : fieldNames) {
//             String fieldValue = params.get(fieldName);
//             if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                 try {
//                     query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
//                             .append('=')
//                             .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
//                 } catch (Exception e) {
//                     System.out.println("Lỗi khi mã hóa tham số: " + fieldName + "=" + fieldValue);
//                 }
//                 if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
//                     query.append('&');
//                 }
//             }
//         }
//         return query.toString();
//     }
// }