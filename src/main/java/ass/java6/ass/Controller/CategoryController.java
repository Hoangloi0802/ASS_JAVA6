package ass.java6.ass.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ass.java6.ass.Entity.Category;
import ass.java6.ass.Entity.Product;
import ass.java6.ass.Repository.CategoryRepository;
import ass.java6.ass.Repository.ProductRepository;
import ass.java6.ass.Service.CategoryService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public String CategoryIndex(Model model) {
        model.addAttribute("cate", new Category());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categoryManage";
    }

    // Trả về JSON cho AJAX
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") String id) {
        Optional<Category> optionalCate = categoryRepository.findById(id);
        if (optionalCate.isPresent()) {
            return ResponseEntity.ok(optionalCate.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Xử lý cả thêm mới và cập nhật
    @PostMapping("/save")
    public String save(@ModelAttribute Category item, RedirectAttributes redirectAttributes) {
        try {
            // Nếu ID rỗng, đặt thành null để JPA sinh tự động (nếu dùng @GeneratedValue)
            if (item.getId() != null && item.getId().isEmpty()) {
                item.setId(null);
            }

            // Kiểm tra trùng ID (nếu không dùng tự động tăng)
            if (item.getId() != null && categoryRepository.existsById(item.getId())) {
                // Nếu đây là thêm mới (không phải cập nhật)
                Optional<Category> existingCategory = categoryRepository.findById(item.getId());
                if (existingCategory.isPresent() && !existingCategory.get().getId().equals(item.getId())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "ID đã tồn tại! Vui lòng chọn ID khác.");
                    return "redirect:/admin/categories";
                }
            }

            categoryService.save(item);
            redirectAttributes.addFlashAttribute("message", "Lưu danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra xem loại sản phẩm có đang được sử dụng bởi sản phẩm nào không
            List<Product> products = productRepository.findByCategoryId(id);
            if (!products.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Không thể xóa loại sản phẩm vì đang được sử dụng bởi " + products.size() + " sản phẩm.");
                return "redirect:/admin/categories";
            }

            // Nếu không có sản phẩm liên quan, tiến hành xóa
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}