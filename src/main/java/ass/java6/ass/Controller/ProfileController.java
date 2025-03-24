package ass.java6.ass.Controller;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Service.ProfileService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProfileController {

    @Autowired
    private ProfileService profileService;

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

        // Cập nhật lại session
        Account refreshedUser = profileService.getCurrentUser(updatedUser.getUsername());
        session.setAttribute("currentUser", refreshedUser);

        return "redirect:/profile";
    }
}
