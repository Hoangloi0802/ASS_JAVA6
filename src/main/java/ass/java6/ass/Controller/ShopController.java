package ass.java6.ass.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ass.java6.ass.Entity.Category;
import ass.java6.ass.Entity.Product;
import ass.java6.ass.Service.CategoryService;
import ass.java6.ass.Service.ProductService;

@Controller
public class ShopController {
    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @GetMapping("/shop")
    public String shop(
            @RequestParam(required = false) Double priceFilter,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
    
        int pageSize = 12;
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
    
        // Gọi service đã lọc/sắp xếp/phân trang
        Page<Product> productPage = productService.filterSortAndPaginate(priceFilter, categoryId, sort, pageable);
    
        List<Category> categories = categoryService.findAll();
    
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("priceFilter", priceFilter);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPageName", "shop");
    
        return "home/sanpham";
    }
    

}