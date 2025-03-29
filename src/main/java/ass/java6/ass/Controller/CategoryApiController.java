package ass.java6.ass.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ass.java6.ass.Entity.Category;
import ass.java6.ass.Repository.CategoryRepository;

@CrossOrigin("*") // Cho phép API được gọi từ các domain khác
@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
