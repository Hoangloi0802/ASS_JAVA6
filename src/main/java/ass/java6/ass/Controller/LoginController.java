package ass.java6.ass.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ass.java6.ass.Config.SendEmailConfig;
import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Dto.DangnhapRequest;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Service.AccoutService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LoginController {
    @Autowired
    private AccoutService accoutService;
    @Autowired
    private SendEmailConfig sendEmailConfig;

    @GetMapping("/Dangky")

    public String Dangky(Model model) {
        model.addAttribute("dangkyaccout", new DangkyRequest());
        return "login/Dangky";
    }

    @PostMapping("/Dangky")
    public String DangkyPost(@Valid @ModelAttribute("dangkyaccout") DangkyRequest dangkyRequest, BindingResult result,
            HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "login/Dangky";
        }
        try {
            Account account = accoutService.Dangky(dangkyRequest);
            session.setAttribute("account", account);
            String otpCode = sendEmailConfig.generateOtp();
            session.setAttribute("otp", otpCode);
            String link = "http://localhost:8080/checkotp"; // Link tới trang nhập OTP
            String subject = "Xác nhận mã OTP để kích hoạt tài khoản";
            String content = sendEmailConfig.generateOtpEmailContent(otpCode, link);
            sendEmailConfig.sendEmail(account.getEmail(), subject, content);
            return "login/otp";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "login/Dangky";
        }

    }

    @GetMapping("/Dangnhap")
    public String dangnhap(Model model) {
        model.addAttribute("dangnhapRequest", new DangkyRequest());
        return "login/Dangnhap";
    }

    @PostMapping("/Dangnhap")
    public String dangnhapPost(@Valid @ModelAttribute("dangnhapRequest") DangnhapRequest dangnhapRequest,
                               BindingResult result,
                               HttpSession session,
                               Model model) {
        if (result.hasErrors()) {
            return "login/Dangnhap";
        }
        try {
            Account acc = accoutService.Dangnhap(dangnhapRequest.getUsername(), dangnhapRequest.getPassword());
            session.setAttribute("user", acc); 
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("dangnhapRequest", dangnhapRequest);
            return "login/Dangnhap";
        }
        return "redirect:/"; 
    }
    
    @GetMapping("/checkotp")
    public String check() {

        return "login/otp";
    }

    @PostMapping("/checkotp")
    public String checkotp(@RequestParam("otpInput") int otpinput, HttpSession session, Model model) {
        accoutService.Xacthuc(session, otpinput);
        model.addAttribute("message", "Xác thực OTP thành công.");
        return "login/Dangnhap";
    }

    @GetMapping("/quenmk")
    public String quenmk() {
        return "login/quenmk";
    }
    @GetMapping("/datlaimk")
    public String datlaimk() {
        return "login/datlaimatkhau";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // clear session
        return "redirect:/Dangnhap?logout";
    }
}
