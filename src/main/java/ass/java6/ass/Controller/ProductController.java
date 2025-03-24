package ass.java6.ass.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class ProductController {
    @GetMapping("/admin/products")
    public String product() {
        return "admin/productManage";
    }
}
