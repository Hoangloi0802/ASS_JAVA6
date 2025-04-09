// package ass.java6.ass.Controller;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import jakarta.servlet.http.HttpServletRequest;  // Sửa import từ javax.servlet sang jakarta.servlet

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import ass.java6.ass.Config.VNPayConfig;
// import ass.java6.ass.Service.VNPayService;

// @Controller
// public class VNPayController {

//     @Autowired
//     private VNPayService vnPayService;

//     @GetMapping("/vnpay/pay")
//     public String showPaymentPage(Model model) {
//         try {
//             String orderId = "ORDER_" + System.currentTimeMillis();
//             model.addAttribute("orderId", orderId);
//             model.addAttribute("amount", 100000); 
//             return "vnpay/pay";
//         } catch (Exception e) {
//             model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
//             return "error";
//         }
//     }

//     @PostMapping("/vnpay/create-payment")
//     public String createPayment(HttpServletRequest request, 
//             @RequestParam("amount") long amount, 
//             @RequestParam("orderId") String orderId,
//             Model model) {
//         try {
//             if (amount <= 0) {
//                 throw new IllegalArgumentException("Số tiền không hợp lệ");
//             }
//             String paymentUrl = vnPayService.createPaymentUrl(request, amount, orderId);
//             return "redirect:" + paymentUrl;
//         } catch (Exception e) {
//             model.addAttribute("error", "Lỗi tạo thanh toán: " + e.getMessage());
//             return "error";
//         }
//     }

//     @GetMapping("/api/vnpay/callback")
//     public String handleCallback(HttpServletRequest request, Model model) {
//         try {
//             Map<String, String> params = new HashMap<>();
//             request.getParameterMap().forEach((key, value) -> 
//                 params.put(key, value[0]));

//             String vnp_SecureHash = params.get("vnp_SecureHash");
//             if (vnp_SecureHash == null) {
//                 throw new IllegalArgumentException("Thiếu chữ ký bảo mật");
//             }

//             params.remove("vnp_SecureHash");
//             params.remove("vnp_SecureHashType");

//             String signValue = vnPayService.hmacSHA512(VNPayConfig.VNP_HASHSECRET, buildQueryString(params));
//             if (!signValue.equals(vnp_SecureHash)) {
//                 throw new IllegalArgumentException("Chữ ký không hợp lệ");
//             }

//             String vnp_ResponseCode = params.get("vnp_ResponseCode");
//             if ("00".equals(vnp_ResponseCode)) {
//                 model.addAttribute("message", "Thanh toán thành công! Mã giao dịch: " + params.get("vnp_TxnRef"));
//             } else {
//                 model.addAttribute("message", "Thanh toán thất bại! Mã lỗi: " + vnp_ResponseCode);
//             }

//             return "vnpay/result";
//         } catch (Exception e) {
//             model.addAttribute("error", "Lỗi xử lý callback: " + e.getMessage());
//             return "error";
//         }
//     }

//     private String buildQueryString(Map<String, String> params) {
//         List<String> fieldNames = new ArrayList<>(params.keySet());
//         Collections.sort(fieldNames);
//         StringBuilder query = new StringBuilder();
//         boolean first = true;
        
//         for (String fieldName : fieldNames) {
//             String fieldValue = params.get(fieldName);
//             if (fieldValue != null && !fieldValue.isEmpty()) {
//                 if (!first) {
//                     query.append('&');
//                 }
//                 query.append(fieldName).append('=').append(fieldValue);
//                 first = false;
//             }
//         }
//         return query.toString();
//     }
// }