package ass.java6.ass.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NhanSuController {
    @GetMapping("/admin/nhansu")
    public String nhansu() {
        return "admin/Quanlynhansu";
    }
}
