package ass.java6.ass.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ass.java6.ass.Entity.Order;
import ass.java6.ass.Entity.OrderDetail;
import ass.java6.ass.Entity.Product;
import ass.java6.ass.Entity.Category;
import ass.java6.ass.Repository.OrderRepository;
import ass.java6.ass.Repository.OrderDetailRepository;

@Controller
public class BillController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @GetMapping("/admin/bill")
    public String getBill(
            Model model,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Order> orderPage;
            Pageable pageable = PageRequest.of(page - 1, size);

            if (search != null && !search.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(search.trim());
                    orderPage = orderRepository.findByIdContaining(id, pageable);
                } catch (NumberFormatException e) {
                    orderPage = orderRepository.findAll(pageable);
                }
            } else {
                orderPage = orderRepository.findAll(pageable);
            }

            model.addAttribute("orders", orderPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orderPage.getTotalPages());
            model.addAttribute("totalItems", orderPage.getTotalElements());
            model.addAttribute("pageSize", size);
            model.addAttribute("search", search);

        } catch (Exception e) {
            System.out.println("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return "admin/billManage";
    }

    @GetMapping("/admin/bill/detail/{id}")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable("id") Long id) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(id);
            Map<String, Object> response = new HashMap<>();

            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("address", order.getAddress());
            orderMap.put("status", order.getStatus()); // Giữ nguyên status dạng String
            orderMap.put("statusDisplay", order.getStatusDisplayName()); // Thêm tên hiển thị
            orderMap.put("createDate", order.getCreateDate());

            if (order.getAccount() != null) {
                Map<String, Object> accountMap = new HashMap<>();
                accountMap.put("username", order.getAccount().getUsername());
                accountMap.put("fullname", order.getAccount().getFullname());
                accountMap.put("email", order.getAccount().getEmail());
                accountMap.put("mobile", order.getAccount().getMobile());
                orderMap.put("account", accountMap);
            }

            if (order.getVoucher() != null) {
                Map<String, Object> voucherMap = new HashMap<>();
                voucherMap.put("id", order.getVoucher().getId());
                voucherMap.put("code", order.getVoucher().getCode());
                voucherMap.put("discountAmount", order.getVoucher().getDiscountAmount());
                orderMap.put("voucher", voucherMap);
            }

            List<Map<String, Object>> orderDetailsList = new ArrayList<>();
            double totalAmount = 0;

            for (OrderDetail detail : orderDetails) {
                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("id", detail.getId());
                detailMap.put("price", detail.getPrice());
                detailMap.put("quantity", detail.getQuantity());

                if (detail.getProduct() != null) {
                    Map<String, Object> productMap = new HashMap<>();
                    Product product = detail.getProduct();
                    productMap.put("id", product.getId());
                    productMap.put("name", product.getName());
                    productMap.put("image", product.getImage());

                    if (product.getCategory() != null) {
                        Map<String, Object> categoryMap = new HashMap<>();
                        Category category = product.getCategory();
                        categoryMap.put("id", category.getId());
                        categoryMap.put("name", category.getName());
                        productMap.put("category", categoryMap);
                    }
                    detailMap.put("product", productMap);
                }

                orderDetailsList.add(detailMap);
                if (detail.getPrice() != null && detail.getQuantity() != null) {
                    totalAmount += detail.getPrice() * detail.getQuantity();
                }
            }

            response.put("order", orderMap);
            response.put("orderDetails", orderDetailsList);
            response.put("totalAmount", totalAmount);

            double discountAmount = 0;
            if (order.getVoucher() != null) {
                discountAmount = order.getVoucher().getDiscountAmount();
                if (discountAmount > totalAmount) {
                    discountAmount = totalAmount;
                }
                response.put("discountAmount", discountAmount);
            }
            response.put("finalAmount", totalAmount - discountAmount);

            System.out.println("Order details count: " + orderDetails.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/admin/bill/update-status/{id}")
    public String updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String newStatus,
            RedirectAttributes redirectAttributes) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng không tồn tại!");
                return "redirect:/admin/bill";
            }

            // Kiểm tra trạng thái hợp lệ
            if (!Order.isValidStatus(newStatus)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Trạng thái không hợp lệ: " + newStatus);
                return "redirect:/admin/bill";
            }

            order.setStatus(newStatus);
            orderRepository.save(order);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật trạng thái đơn hàng thành " + order.getStatusDisplayName() + " thành công!");
            return "redirect:/admin/bill";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/bill";
        }
    }

    @PostMapping("/admin/bill/update-status-ajax/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrderStatusAjax(
            @PathVariable("id") Long id,
            @RequestParam("status") String newStatus) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                response.put("success", false);
                response.put("message", "Đơn hàng không tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra trạng thái hợp lệ
            if (!Order.isValidStatus(newStatus)) {
                response.put("success", false);
                response.put("message", "Trạng thái không hợp lệ: " + newStatus);
                return ResponseEntity.badRequest().body(response);
            }

            order.setStatus(newStatus);
            orderRepository.save(order);

            response.put("success", true);
            response.put("status", order.getStatus());
            response.put("statusDisplay", order.getStatusDisplayName());
            response.put("message",
                    "Cập nhật trạng thái đơn hàng thành " + order.getStatusDisplayName() + " thành công!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/admin/bill/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                response.put("success", false);
                response.put("message", "Đơn hàng không tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }

            if (Order.STATUS_CART.equals(order.getStatus())) {
                response.put("success", false);
                response.put("message", "Không thể xóa giỏ hàng tạm thời!");
                return ResponseEntity.badRequest().body(response);
            }

            if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                orderDetailRepository.deleteByOrderId(id);
                orderRepository.delete(order);
                response.put("success", true);
                response.put("message", "Xóa đơn hàng và chi tiết đơn hàng thành công!");
            } else {
                orderRepository.delete(order);
                response.put("success", true);
                response.put("message", "Xóa đơn hàng thành công!");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi xóa đơn hàng: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
    }
}