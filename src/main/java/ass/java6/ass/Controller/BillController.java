package ass.java6.ass.Controller;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String getBill(Model model) {
        try {
            List<Order> orders = orderRepository.findAll();
            model.addAttribute("orders", orders);
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

            // Lấy chi tiết đơn hàng trực tiếp từ repository
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(id);

            // Tạo response map để tránh circular references
            Map<String, Object> response = new HashMap<>();

            // Thông tin cơ bản của order
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("address", order.getAddress());
            orderMap.put("status", order.isStatus());
            orderMap.put("createDate", order.getCreateDate());

            // Thông tin account
            if (order.getAccount() != null) {
                Map<String, Object> accountMap = new HashMap<>();
                accountMap.put("username", order.getAccount().getUsername());
                accountMap.put("fullname", order.getAccount().getFullname());
                accountMap.put("email", order.getAccount().getEmail());
                accountMap.put("mobile", order.getAccount().getMobile());
                orderMap.put("account", accountMap);
            }

            // Thông tin voucher
            if (order.getVoucher() != null) {
                Map<String, Object> voucherMap = new HashMap<>();
                voucherMap.put("id", order.getVoucher().getId());
                voucherMap.put("code", order.getVoucher().getCode());
                voucherMap.put("discountAmount", order.getVoucher().getDiscountAmount());
                orderMap.put("voucher", voucherMap);
            }

            // Tạo danh sách chi tiết đơn hàng đã được đơn giản hóa
            List<Map<String, Object>> orderDetailsList = new ArrayList<>();
            double totalAmount = 0;

            for (OrderDetail detail : orderDetails) {
                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("id", detail.getId());
                detailMap.put("price", detail.getPrice());
                detailMap.put("quantity", detail.getQuantity());

                // Thông tin sản phẩm
                if (detail.getProduct() != null) {
                    Map<String, Object> productMap = new HashMap<>();
                    Product product = detail.getProduct();
                    productMap.put("id", product.getId());
                    productMap.put("name", product.getName());
                    productMap.put("image", product.getImage());

                    // Thông tin danh mục
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

                // Tính tổng tiền
                if (detail.getPrice() != null && detail.getQuantity() != null) {
                    totalAmount += detail.getPrice() * detail.getQuantity();
                }
            }

            // Thêm vào response
            response.put("order", orderMap);
            response.put("orderDetails", orderDetailsList);
            response.put("totalAmount", totalAmount);

            // Tính giảm giá nếu có voucher
            double discountAmount = 0;
            if (order.getVoucher() != null) {
                discountAmount = totalAmount * order.getVoucher().getDiscountAmount() / 100;
                response.put("discountAmount", discountAmount);
            }

            // Tính tổng thanh toán
            response.put("finalAmount", totalAmount - discountAmount);

            System.out.println("Order details count: " + orderDetails.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/admin/bill/update-status/{id}")
    public String updateOrderStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng không tồn tại!");
                return "redirect:/admin/bill";
            }
            // Toggle the status
            order.setStatus(!order.isStatus());
            orderRepository.save(order);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật trạng thái đơn hàng thành " + (order.isStatus() ? "Đã thanh toán" : "Chưa thanh toán")
                            + " thành công!");
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
    public ResponseEntity<Map<String, Object>> updateOrderStatusAjax(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                response.put("success", false);
                response.put("message", "Đơn hàng không tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }

            // Toggle the status
            order.setStatus(!order.isStatus());
            orderRepository.save(order);

            response.put("success", true);
            response.put("status", order.isStatus());
            response.put("message", "Cập nhật trạng thái đơn hàng thành " +
                    (order.isStatus() ? "Đã thanh toán" : "Chưa thanh toán") + " thành công!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/admin/bill/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng không tồn tại!");
                return "redirect:/admin/bill";
            }
            if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Không thể xóa đơn hàng vì có sản phẩm liên quan!");
            } else {
                orderRepository.delete(order);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa đơn hàng thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/bill";
    }

    
    
}
