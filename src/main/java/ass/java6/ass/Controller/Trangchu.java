package ass.java6.ass.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;

import ass.java6.ass.Entity.Product;
import ass.java6.ass.Service.ProductService;

@Controller

public class Trangchu {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> topSellingProducts = productService.findTopSellingProducts(8);
        model.addAttribute("topSellingProducts", topSellingProducts);
        model.addAttribute("currentPage", "home");

        List<Product> latestProducts = productService.findLatestProducts(8);
        model.addAttribute("latestProducts", latestProducts);
        return "home/trangchu"; // view name
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model) {
        Product product = productService.findById1(id).orElse(null);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        return "home/chitiet";
    }

}