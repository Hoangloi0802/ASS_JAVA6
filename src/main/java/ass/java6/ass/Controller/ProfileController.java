package ass.java6.ass.Controller;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Repository.AccountRepository;
import ass.java6.ass.Service.ProfileService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@ControllerAdvice
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AccountRepository accountRepository;
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser != null) {
            Account freshUser = profileService.getCurrentUser(currentUser.getUsername());
            model.addAttribute("user", freshUser);
        }
        return "home/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") Account updatedUser,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        profileService.updateProfile(updatedUser, file);

        Account refreshedUser = profileService.getCurrentUser(updatedUser.getUsername());
        session.setAttribute("currentUser", refreshedUser);

        return "redirect:/profile";
    }

    @ModelAttribute("currentUser")
    public Account currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
            return accountRepository.findByUsername(username).orElse(null); // Truy vấn database để lấy đầy đủ thông tin
        }
        return null;
    }
    

}
