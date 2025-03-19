package ass.java6.ass.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/admin/account")
    public String account() {
        return "admin/accountManage";
    }


    @GetMapping("/admin/products")
    public String product() {
        return "admin/productManage";
    }

    @GetMapping("/admin/bill")
    public String bill() {
        return "admin/billManage";
    }

    @GetMapping("/admin/statistics")
    public String sattistics() {
        return "admin/statistics";
    }

    @GetMapping("/admin/nhansu")
    public String nhansu() {
        return "admin/Quanlynhansu";
    }
}