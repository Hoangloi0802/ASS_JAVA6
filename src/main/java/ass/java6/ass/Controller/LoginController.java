package ass.java6.ass.Controller;

import java.security.Principal;

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
import ass.java6.ass.Dto.DatlaimkRequest;
import ass.java6.ass.Dto.ThaydoimatkhauRequeset;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Repository.AccountRepository;
import ass.java6.ass.Service.AccoutService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LoginController {

    @Autowired
    private AccountRepository accountRepository;
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
            session.removeAttribute("otp");
            String otpCode = sendEmailConfig.generateOtp();
            session.setAttribute("otp", otpCode);
            System.out.println("📧 Gửi OTP đến email: " + account.getEmail() + " - Mã OTP: " + otpCode);
            String link = "http://localhost:8080/checkotp";
            String subject = "Xác nhận mã OTP để kích hoạt tài khoản";
            String content = sendEmailConfig.generateOtpEmailContent(otpCode, link);
            sendEmailConfig.sendEmail(account.getEmail(), subject, content);
            return "login/otp";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login/Dangky";
        }
    }

    @GetMapping("/Dangnhap")
    public String dangnhap(Model model) {
    model.addAttribute("dangnhapRequest", new DangnhapRequest());
        return "login/Dangnhap";
    }

    @GetMapping("/checkotp")
    public String check() {
        return "login/otp";
    }

    @PostMapping("/checkotp")
    public String checkotp(@RequestParam(value = "otpInput", required = false) Integer otpinput, HttpSession session,
            Model model) {
        if (otpinput == null) {
            model.addAttribute("errorMessage", "OTP không được để trống.");
            return "login/otp";
        }
        try {
            accoutService.Xacthuc(session, otpinput);
            model.addAttribute("message", "Xác thực OTP thành công.");
            return "redirect:/login/Dangnhap";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login/otp";
        }

    }

    @GetMapping("/otpquenmk")
    public String checkquenmk() {

        return "login/otpquenmk";
    }

    @PostMapping("/otpquenmk")
    public String checkquenmkPost(@RequestParam(value = "otpInput", required = false) Integer otpInput, HttpSession session, Model model) {
        if (otpInput == null) {
            model.addAttribute("errorMessage", "OTP không được để trống.");
            return "login/otpquenmk";
        }
        try {
            accoutService.Checkotpquenmk(session, otpInput);
            return "redirect:/datlaimk";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login/otpquenmk";
        }
    }

    @GetMapping("/quenmk")
    public String quenmk() {

        return "login/quenmk";
    }

    @PostMapping("/quenmk")
    public String quenmkPost(Model model, @RequestParam("emailInput") String email, HttpSession session) {
        if (email == null) {
            model.addAttribute("message", "email không được để trống");
            return "login/quenmk";

        }
        if (accountRepository.findByEmail(email).isPresent()) {
            String otp = sendEmailConfig.generateOtp();
            session.setAttribute("otp", otp);
            session.setAttribute("emailcheck", email);
            String link = "http://localhost:8080/quenmk";
            String subject = "Xác nhận mã OTP để kích hoạt tài khoản";
            String content = sendEmailConfig.generateOtpEmailContent(otp, link);
            sendEmailConfig.sendEmail(email, subject, content);
            return "login/otpquenmk";
        } else {
            model.addAttribute("message", "email không tồn tại");
        }
        return "login/quenmk";
    }

    @PostMapping("/datlaimk")
    public String datlaimkPost(@Valid @ModelAttribute("datlaimkRequest") DatlaimkRequest datlaimkRequest,

            BindingResult result, HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "login/datlaimatkhau";
        }

        if(!datlaimkRequest.getPassword().equals(datlaimkRequest.getConfirmPassword())){
            model.addAttribute("errorMessage", "mật khẩu nhập lại không khớp");
            return "login/datlaimatkhau";
        }
        String email = (String) session.getAttribute("emailcheck");
        if (email == null) {
            model.addAttribute("message", "Không tìm thấy email, vui lòng thử lại.");
            return "login/datlaimatkhau";
        }
        try {
            accoutService.doiMatKhau(email, datlaimkRequest.getPassword());
            session.removeAttribute("emailcheck");
            return "redirect:/login?success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            return "login/datlaimatkhau";
        }
    }
    @GetMapping("/datlaimk")

    public String datlaimk(Model model) {
        model.addAttribute("datlaimkRequest", new DatlaimkRequest());
        return "login/datlaimatkhau";
    }

    @PostMapping("/goilaiotp")
    public String resendOtp(HttpSession session, Model model) {
        try {
            // Lấy tài khoản từ session
            Account account = (Account) session.getAttribute("account");
            if (account == null) {
                model.addAttribute("errorMessage", "Không tìm thấy tài khoản để gửi lại OTP.");
                return "login/otp";
            }
            session.removeAttribute("otp");

            // Tạo OTP mới
            String otpCode = sendEmailConfig.generateOtp();
            session.setAttribute("otp", otpCode);

            // Gửi email OTP mới
            String link = "http://localhost:8080/checkotp";
            String subject = "🔐 Xác nhận mã OTP mới";
            String content = sendEmailConfig.generateOtpEmailContent(otpCode, link);
            sendEmailConfig.sendEmail(account.getEmail(), subject, content);

            
            System.out.println("📧 Gửi lại OTP đến email: " + account.getEmail() + " - Mã OTP mới: " + otpCode);

            model.addAttribute("message", "Mã OTP mới đã được gửi đến email của bạn.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi gửi lại OTP. Vui lòng thử lại!");
        }
        return "login/otp";
    }
    @GetMapping("/doimatkhau")
    public String tdmk(Model model){
        model.addAttribute("tdmk",new ThaydoimatkhauRequeset());
        return "login/doimatkhau";
    }
    @PostMapping("/doimatkhau")
    public String posttdmk(@Valid @ModelAttribute("tdmk") ThaydoimatkhauRequeset thaydoimatkhauRequeset,BindingResult result,Model model,Principal principal){
        if(result.hasErrors()){
            return "login/doimatkhau";
        }
        try {
            accoutService.thaydoimatkhau(principal.getName(), thaydoimatkhauRequeset.getCurrentPassword(), thaydoimatkhauRequeset.getNewPassword(), thaydoimatkhauRequeset.getConfirmPassword());
            return "redirect:/";
        } catch (IllegalArgumentException  e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
       
        return "login/doimatkhau";
    }
    


}
