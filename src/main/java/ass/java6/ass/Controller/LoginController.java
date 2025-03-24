package ass.java6.ass.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ass.java6.ass.Config.SendEmailConfig;
import ass.java6.ass.Dto.DangkyRequest;  
import ass.java6.ass.Dto.DatlaimkRequest;
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
    public String dangnhap(Model model ,@RequestParam(value = "error", required = false)String error) {
        if (error != null) {
            if ("invalid_credentials".equals(error)) {
                model.addAttribute("errorMessage", "Thông tin đăng nhập không hợp lệ!");
            } else {
                model.addAttribute("errorMessage", "Đã xảy ra lỗi không xác định!");
            }
        }
        model.addAttribute("dangnhapRequest", new DangkyRequest());
        return "login/Dangnhap";
    }    
    @GetMapping("/checkotp")
    public String check() {
        return "login/otp";
    }
    @PostMapping("/checkotp")
    public String checkotp(@RequestParam(value = "otpInput", required = false) Integer otpinput, HttpSession session, Model model) {
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
    public String checkquenmkPost(@RequestParam("otpInput") String otpInput, HttpSession session, Model model) {
        try {
            accoutService.Checkotpquenmk(session, otpInput);
            return "login/datlaimatkhau";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", e.getMessage());
            return "login/otpquenmk";
        }
    }
    @GetMapping("/quenmk")
    public String quenmk() {
        
        return "login/quenmk";
    }
    @PostMapping("/quenmk")
    public String quenmkPost(Model model ,@RequestParam("emailInput") String email ,HttpSession session){
        if(email == null){
            model.addAttribute("message", "email không được để trống");
            return "login/otp";
            
        }
        if(accountRepository.findByEmail(email).isPresent()){
            String otp = sendEmailConfig.generateOtp();
            session.setAttribute("otp", otp);
            session.setAttribute("emailcheck" ,email);
            String link = "http://localhost:8080/quenmk";
            String subject = "Xác nhận mã OTP để kích hoạt tài khoản";
            String content = sendEmailConfig.generateOtpEmailContent(otp, link);
            sendEmailConfig.sendEmail(email, subject, content);
            return "login/otpquenmk";
        }else{
            model.addAttribute("message", "email không tồn tại");
        }
         return "login/otpquenmk";
    }
    
    @PostMapping("/datlaimk")
    public String datlaimkPost(@Valid @ModelAttribute("datlaimkRequest") DatlaimkRequest datlaimkRequest ,BindingResult result, HttpSession session, Model model) {
        if(result.hasErrors()){
             return "login/datlaimatkhau";
        }
        String email = (String) session.getAttribute("emailcheck");
        if (email == null) {
            model.addAttribute("message", "Không tìm thấy email, vui lòng thử lại.");
            return "login/datlaimatkhau";
        }
        if (!datlaimkRequest.getPassword().equals(datlaimkRequest.getConfirmPassword())) {
            model.addAttribute("message", "Mật khẩu xác nhận không khớp.");
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
        
        // Debug
        System.out.println("📧 Gửi lại OTP đến email: " + account.getEmail() + " - Mã OTP mới: " + otpCode);
        
        model.addAttribute("message", "Mã OTP mới đã được gửi đến email của bạn.");
    } catch (Exception e) {
        model.addAttribute("errorMessage", "Có lỗi xảy ra khi gửi lại OTP. Vui lòng thử lại!");
    }

    return "login/otp";
}


    
    
}
