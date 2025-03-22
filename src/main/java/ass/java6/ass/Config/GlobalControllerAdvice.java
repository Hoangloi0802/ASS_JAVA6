package ass.java6.ass.Config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import ass.java6.ass.Entity.Account;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void injectUserInfo(HttpSession session, Model model) {
        Account acc = (Account) session.getAttribute("user");
        if (acc != null) {
            model.addAttribute("fullname", acc.getFullname());
            model.addAttribute("photo", acc.getPhoto());
            model.addAttribute("role", acc.getRole().name());
        }
    }
}
