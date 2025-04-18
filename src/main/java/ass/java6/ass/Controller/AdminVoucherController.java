package ass.java6.ass.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ass.java6.ass.Entity.Voucher;
import ass.java6.ass.Entity.Category;
import ass.java6.ass.Service.VoucherService;
import ass.java6.ass.Service.CategoryService;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/nhansu")
public class AdminVoucherController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listVouchers(Model model) {
        List<Voucher> vouchers = voucherService.findAll();
        List<Category> categories = categoryService.findAll();
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("categories", categories);
        return "admin/Quanlynhansu";
    }

    @PostMapping("/save")
    public String saveVoucher(@ModelAttribute("voucher") Voucher voucher, BindingResult result,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("errorMessage", "Có lỗi xảy ra! Vui lòng kiểm tra lại.");
            return "redirect:/admin/nhansu";
        }
        Date currentDate = new Date(System.currentTimeMillis());
        if (voucher.getExpiryDate().before(currentDate) && voucher.getTrangThai()) {
            redirect.addFlashAttribute("errorMessage", "Không thể lưu voucher đã hết hạn với trạng thái Hoạt động!");
            return "redirect:/admin/nhansu";
        }

        voucherService.save(voucher);
        redirect.addFlashAttribute("successMessage", "Lưu voucher thành công!");
        return "redirect:/admin/nhansu";
    }

    @GetMapping("/create-auto-discount")
    public String createAutoDiscountVoucher(RedirectAttributes redirect) {
        Voucher voucher = new Voucher();
        voucher.setCode("AUTO50K_" + System.currentTimeMillis());
        voucher.setDiscountAmount(100000.0);
        voucher.setMinOrderValue(1000000.0);
        voucher.setExpiryDate(Date.valueOf("2025-12-31"));
        voucher.setCategory(null);
        voucher.setTrangThai(true);

        voucherService.save(voucher);
        redirect.addFlashAttribute("successMessage", "Đã tạo voucher giảm 100k cho đơn hàng trên 1 triệu!");
        return "redirect:/admin/nhansu";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Voucher getVoucher(@PathVariable Integer id) {
        return voucherService.findById(id).orElse(null);
    }

    @PostMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable Integer id, RedirectAttributes redirect) {
        voucherService.deleteById(id);
        redirect.addFlashAttribute("successMessage", "Xóa voucher thành công!");
        return "redirect:/admin/nhansu";
    }

    @PostMapping("/update-expired")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateExpiredVouchers() {
        Map<String, String> response = new HashMap<>();
        try {
            voucherService.updateExpiredVouchers();
            response.put("message", "Đã cập nhật trạng thái các voucher hết hạn thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}