package ass.java6.ass.Controller;

import org.springframework.ui.Model;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ass.java6.ass.Entity.Category;
import ass.java6.ass.Repository.CategoryRepository;
import ass.java6.ass.Service.CategoryService;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String CategoryIndex(Model model) {
        model.addAttribute("cate", new Category());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categoryManage";
    }

    @GetMapping("/{id}")
    public String CategoryEdit(Model model, @PathVariable("id") String id) {
        Optional<Category> optionalCate = categoryRepository.findById(id);
        if (optionalCate.isPresent()) {
            model.addAttribute("cate", optionalCate.get());
        } else {
            model.addAttribute("cate", new Category());
        }
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categoryManage";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Category item) {
        categoryService.save(item);
        return "redirect:/admin/categories";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Category item) {
        categoryService.save(item);
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") String id) {
        categoryService.deleteById(id);
        return "redirect:/admin/categories";
    }
}
