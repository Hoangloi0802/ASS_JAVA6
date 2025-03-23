package ass.java6.ass.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public String home(Model model) {
       // Sản phẩm bán chạy
       List<Product> topSellingProducts = productService.findTopSellingProducts(8);
       model.addAttribute("topSellingProducts", topSellingProducts);
       model.addAttribute("currentPage", "home");
       // Sản phẩm mới nhất
       List<Product> latestProducts = productService.findLatestProducts(8);
       model.addAttribute("latestProducts", latestProducts);
        return "home/trangchu"; // view name
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


    @GetMapping("/thanhtoan")
    public String thanhtoan() {
        return "home/thanhtoan";
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