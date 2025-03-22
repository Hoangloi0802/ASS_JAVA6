package ass.java6.ass.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ass.java6.ass.Entity.Product;
import ass.java6.ass.Service.ProductService;

@Controller
public class Trangchu {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String homePage(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("currentPage", "home");
        return "home/trangchu";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/"; // Không tìm thấy thì về Home
        }
        model.addAttribute("product", product);
        return "home/chitiet"; // Tên file HTML chi tiết
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchByName(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "home/sanpham"; 
    }

    @GetMapping("/thanhtoan")
    public String thanhtoan() {
        return "home/thanhtoan";
    }

    @GetMapping("/chitiet")
    public String chitiet() {
        return "home/chitiet";
    }

    @GetMapping("/profile")
    public String profile() {
        return "home/profile";
    }

    @GetMapping("/donmua")
    public String donmua() {
        return "home/donmua";
    }

    @GetMapping("/donhang")
    public String donhang() {
        return "home/donhang";
    }

    @GetMapping("/chitietdonhang")
    public String ctdonhang() {
        return "home/chitietdonhang";
    }

}