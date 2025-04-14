package ass.java6.ass.Controller;

import ass.java6.ass.Config.VNPayConfig;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Order;
import ass.java6.ass.Entity.OrderDetail;
import ass.java6.ass.Service.AccoutService;
import ass.java6.ass.Service.CartService;
import ass.java6.ass.Service.ProductService;
import ass.java6.ass.Service.VNPayService;
import ass.java6.ass.Service.MomoService;
import ass.java6.ass.Service.MomoService.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PayController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AccoutService accountService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private MomoService momoService;

    @Autowired
    private ProductService productService;

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
    public String placeOrder(
            @RequestParam("address") String address,
            @RequestParam("paymentMethod") String paymentMethod,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {
        Account account = getAuthenticatedAccount();
        if (account == null) {
            return "redirect:/dangnhap";
        }

        try {
            // Tạo đơn hàng tạm thời
            Order order = cartService.createTemporaryOrder(account, address);
            List<OrderDetail> orderDetails = order.getOrderDetails();

            // Tính toán tổng tiền
            double subtotal = orderDetails.stream()
                    .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                    .sum();
            double discount = (order.getVoucher() != null) ? order.getVoucher().getDiscountAmount() : 0.0;
            double shippingFee = 50000;
            double totalAmount = Math.max(subtotal - discount + shippingFee, 0);
            long amount = (long) totalAmount;

            if (amount < 5000 || amount >= 1_000_000_000) {
                redirectAttributes.addFlashAttribute("error", "Số tiền không hợp lệ!");
                return "redirect:/thanhtoan";
            }

            // Lưu đơn hàng tạm thời
            cartService.saveOrder(order);
            request.getSession().setAttribute("tempOrder", order);
            request.getSession().setAttribute("account", account);
            request.getSession().setAttribute("totalAmount", totalAmount);

            if ("vnpay".equalsIgnoreCase(paymentMethod)) {
                model.addAttribute("orderId", "ORDER_" + order.getId());
                model.addAttribute("subtotal", subtotal);
                model.addAttribute("discount", discount);
                model.addAttribute("shippingFee", shippingFee);
                model.addAttribute("totalAmount", totalAmount);
                model.addAttribute("amount", amount);
                return "pay";
            } else if ("momo".equalsIgnoreCase(paymentMethod)) {
                PaymentResponse response = momoService.createPaymentRequest(String.valueOf(amount));
                if (response.getResultCode() == 0) {
                    return "redirect:" + response.getPayUrl();
                } else {
                    redirectAttributes.addFlashAttribute("error", "Lỗi MoMo: " + response.getMessage());
                    return "redirect:/thanhtoan";
                }
            } else {
                // Thanh toán thường
                boolean stockUpdated = productService.decreaseStockForOrder(orderDetails);
                if (!stockUpdated) {
                    redirectAttributes.addFlashAttribute("error", "Không đủ hàng trong kho!");
                    order.setStatus("FAILED");
                    cartService.saveOrder(order);
                    return "redirect:/thanhtoan";
                }
                order.setStatus("SHIPPING");
                cartService.saveOrder(order);
                cartService.clearCart(account);
                return "redirect:/thanhtoan/thanhcong?orderId=" + order.getId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi đặt hàng: " + e.getMessage());
            return "redirect:/thanhtoan";
        }
    }

    @PostMapping("/vnpay/create-payment")
    public String createPayment(
            @RequestParam("amount") long amount,
            @RequestParam("orderId") String orderId,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            Order tempOrder = (Order) request.getSession().getAttribute("tempOrder");
            Double totalAmount = (Double) request.getSession().getAttribute("totalAmount");
            if (tempOrder == null || totalAmount == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng hoặc tổng tiền!");
                return "redirect:/thanhtoan";
            }
            if (amount != (long) totalAmount.doubleValue()) {
                redirectAttributes.addFlashAttribute("error", "Số tiền không khớp!");
                return "redirect:/thanhtoan";
            }
            String paymentUrl = vnPayService.createPaymentUrl(request, amount, "ORDER_" + tempOrder.getId());
            return "redirect:" + paymentUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo URL thanh toán: " + e.getMessage());
            return "redirect:/thanhtoan";
        }
    }

    @GetMapping("/thanhtoan/thanhcong")
    public String orderSuccess(@RequestParam(value = "orderId", required = false) String orderId, Model model) {
        if (orderId == null || orderId.trim().isEmpty()) {
            model.addAttribute("error", "Mã đơn hàng không hợp lệ");
            return "redirect:/";
        }

        Long orderIdLong;
        try {
            orderIdLong = Long.parseLong(orderId);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Mã đơn hàng không đúng định dạng");
            return "redirect:/";
        }

        Order order = cartService.getOrderById(orderIdLong);
        if (order == null) {
            model.addAttribute("error", "Không tìm thấy đơn hàng");
            return "redirect:/";
        }

        List<OrderDetail> orderDetails = order.getOrderDetails();
        if (orderDetails == null || orderDetails.isEmpty()) {
            model.addAttribute("error", "Đơn hàng không có chi tiết");
        }

        double subtotal = orderDetails.stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();
        double discount = (order.getVoucher() != null) ? order.getVoucher().getDiscountAmount() : 0.0;
        double shippingFee = 50000;
        double totalAmount = Math.max(subtotal - discount + shippingFee, 0);

        model.addAttribute("order", order);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("totalAmount", totalAmount);

        return "home/donmua";
    }

    @GetMapping("/donhang")
    public String listOrders(Model model, @RequestParam(defaultValue = "0") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Order> orderPage = cartService.getOrdersByUsernamePaginated(username, pageable);

        double grandTotal = cartService.getOrdersByUsername(username).stream()
                .filter(order -> !List.of(Order.STATUS_PENDING, Order.STATUS_CART).contains(order.getStatus()))
                .mapToDouble(order -> cartService.calculateOrderTotal(order))
                .sum();

        Map<Long, Double> totalMap = new HashMap<>();
        for (Order order : orderPage.getContent()) {
            totalMap.put(order.getId(), cartService.calculateOrderTotal(order));
        }

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("totalMap", totalMap);
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("pageSize", pageSize);

        return "home/donhang";
    }

    @GetMapping("/api/vnpay/callback")
    public String handleVNPayCallback(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        if (request == null || request.getParameterMap() == null) {
            redirectAttributes.addFlashAttribute("message", "Yêu cầu không hợp lệ!");
            return "redirect:/thanhtoan";
        }

        Map<String, String> params = new HashMap<>();
        for (String key : request.getParameterMap().keySet()) {
            String[] values = request.getParameterValues(key);
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        }

        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash == null) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy chữ ký!");
            return "redirect:/thanhtoan";
        }

        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        Order tempOrder = (Order) request.getSession().getAttribute("tempOrder");
        Double totalAmount = (Double) request.getSession().getAttribute("totalAmount");
        if (tempOrder == null || totalAmount == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng tạm thời hoặc tổng tiền!");
            return "redirect:/thanhtoan";
        }

        try {
            String signValue = vnPayService.hmacSHA512(VNPayConfig.VNP_HASHSECRET, buildQueryString(params));
            if (signValue.equals(vnp_SecureHash)) {
                String vnp_ResponseCode = params.get("vnp_ResponseCode");
                String orderId = params.get("vnp_TxnRef").replace("ORDER_", "");

                if (!orderId.equals(String.valueOf(tempOrder.getId()))) {
                    redirectAttributes.addFlashAttribute("error", "Mã đơn hàng không khớp!");
                    return "redirect:/thanhtoan";
                }

                if ("00".equals(vnp_ResponseCode)) {
                    boolean stockUpdated = productService.decreaseStockForOrder(tempOrder.getOrderDetails());
                    if (!stockUpdated) {
                        tempOrder.setStatus("FAILED");
                        cartService.saveOrder(tempOrder);
                        redirectAttributes.addFlashAttribute("error", "Không đủ hàng trong kho!");
                        return "redirect:/thanhtoan";
                    }
                    tempOrder.setStatus("SHIPPING");
                    cartService.saveOrder(tempOrder);
                    Account account = (Account) request.getSession().getAttribute("account");
                    if (account != null) {
                        cartService.clearCart(account);
                    }
                    redirectAttributes.addFlashAttribute("message",
                            "Thanh toán VNPay thành công! Tổng tiền: " + totalAmount);
                } else {
                    tempOrder.setStatus("FAILED");
                    cartService.saveOrder(tempOrder);
                    redirectAttributes.addFlashAttribute("error",
                            "Thanh toán VNPay thất bại! Mã lỗi: " + vnp_ResponseCode);
                }
            } else {
                tempOrder.setStatus("FAILED");
                cartService.saveOrder(tempOrder);
                redirectAttributes.addFlashAttribute("error", "Chữ ký không hợp lệ!");
            }
            request.getSession().removeAttribute("tempOrder");
            request.getSession().removeAttribute("account");
            request.getSession().removeAttribute("totalAmount");
            return "redirect:/thanhtoan/thanhcong?orderId=" + tempOrder.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý VNPay callback: " + e.getMessage());
            return "redirect:/thanhtoan/thanhcong?orderId=" + tempOrder.getId();
        }
    }

    @GetMapping("/thanhtoan/momo/return")
    public String handleMoMoReturn(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String resultCode = request.getParameter("resultCode");
        Order tempOrder = (Order) request.getSession().getAttribute("tempOrder");
        Double totalAmount = (Double) request.getSession().getAttribute("totalAmount");

        if (tempOrder == null || totalAmount == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng tạm thời hoặc tổng tiền!");
            return "redirect:/thanhtoan";
        }

        try {
            if ("0".equals(resultCode)) {
                boolean stockUpdated = productService.decreaseStockForOrder(tempOrder.getOrderDetails());
                if (!stockUpdated) {
                    tempOrder.setStatus("FAILED");
                    cartService.saveOrder(tempOrder);
                    redirectAttributes.addFlashAttribute("error", "Không đủ hàng trong kho!");
                    return "redirect:/thanhtoan";
                }
                tempOrder.setStatus("SHIPPING");
                cartService.saveOrder(tempOrder);
                Account account = (Account) request.getSession().getAttribute("account");
                if (account != null) {
                    cartService.clearCart(account);
                }
                redirectAttributes.addFlashAttribute("message",
                        "Thanh toán MoMo thành công! Tổng tiền: " + totalAmount + " VNĐ");
            } else {
                tempOrder.setStatus("FAILED");
                cartService.saveOrder(tempOrder);
                redirectAttributes.addFlashAttribute("error",
                        "Thanh toán MoMo thất bại! Mã lỗi: " + resultCode);
            }
            request.getSession().removeAttribute("tempOrder");
            request.getSession().removeAttribute("account");
            request.getSession().removeAttribute("totalAmount");
            return "redirect:/thanhtoan/thanhcong?orderId=" + tempOrder.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý MoMo return: " + e.getMessage());
            request.getSession().removeAttribute("tempOrder");
            request.getSession().removeAttribute("account");
            request.getSession().removeAttribute("totalAmount");
            return "redirect:/thanhtoan/thanhcong?orderId=" + tempOrder.getId();
        }
    }

    @GetMapping("/chitietdonhang/{orderId}")
    public String viewOrder(@PathVariable("orderId") Long orderId, Model model) {
        Order order = cartService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/";
        }

        double subtotal = cartService.calculateSubtotal(order);
        double discount = cartService.calculateDiscount(order);
        double shippingFee = 50000;
        double totalAmount = cartService.calculateOrderTotal(order);

        model.addAttribute("order", order);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("totalAmount", totalAmount);

        return "home/chitietdonhang";
    }

    @PostMapping("/chitietdonhang/{id}/confirm-received")
    public String confirmReceived(@PathVariable("id") Long id) {
        cartService.confirmOrderReceived(id);
        return "redirect:/chitietdonhang/" + id;
    }

    @PostMapping("/chitietdonhang/{id}/cancel")
    public String cancelOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Account account = getAuthenticatedAccount();
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để thực hiện hành động này!");
            return "redirect:/dangnhap";
        }

        Order order = cartService.getOrderById(id);
        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
            return "redirect:/donhang";
        }

        if (!order.getAccount().getUsername().equals(account.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền hủy đơn hàng này!");
            return "redirect:/donhang";
        }

        if (!order.getStatus().equals(Order.STATUS_PENDING) && !order.getStatus().equals(Order.STATUS_SHIPPING)) {
            redirectAttributes.addFlashAttribute("error", "Đơn hàng không thể hủy ở trạng thái hiện tại!");
            return "redirect:/chitietdonhang/" + id;
        }

        order.setStatus(Order.STATUS_FAILED);
        cartService.saveOrder(order);

        redirectAttributes.addFlashAttribute("message", "Đơn hàng đã được hủy thành công!");
        return "redirect:/chitietdonhang/" + id;
    }

    private Account getAuthenticatedAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? accountService.findByUsername(auth.getName()).orElse(null)
                : null;
    }

    private String buildQueryString(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (Exception e) {
                    System.out.println("Lỗi khi mã hóa tham số: " + fieldName + "=" + fieldValue);
                }
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    query.append('&');
                }
            }
        }
        return query.toString();
    }
}