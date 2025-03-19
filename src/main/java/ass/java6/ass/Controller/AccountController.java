package ass.java6.ass.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
    @GetMapping("/admin/account")
    public String account() {
        return "admin/accountManage";
    }
}
