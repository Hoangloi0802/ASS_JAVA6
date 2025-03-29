package ass.java6.ass.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ass.java6.ass.Entity.Order;
import ass.java6.ass.Repository.OrderRepository;

@Controller
public class BillController {
    @Autowired
    OrderRepository orderRepository;

  

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

    @GetMapping("/admin/bill/delete/{id}")
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
