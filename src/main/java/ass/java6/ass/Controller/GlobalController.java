package ass.java6.ass.Controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import ass.java6.ass.Entity.Account;
import jakarta.servlet.http.HttpSession;
@ControllerAdvice
public class GlobalController {
@ModelAttribute
    public void addUserToModel(HttpSession session, org.springframework.ui.Model model) {
        Account user = (Account) session.getAttribute("user");
        model.addAttribute("user", user); 
    }
}
