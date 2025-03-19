package ass.java6.ass.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ass.java6.ass.Config.SendEmailConfig;
import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Dto.DangnhapRequest;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Service.AccoutService;


@Controller
public class Trangchu {
    @Autowired
    private AccoutService accoutService;
    @Autowired
    private SendEmailConfig sendEmailConfig;
    @GetMapping("/")
    public String getMethodName(Model model) {
        return "home/trangchu";
    }

    @GetMapping("/shop")
    public String shop(Model model) {
        return "home/sanpham";
    }

    @GetMapping("/Dangnhap")
    public String dangnhap(Model model) {
        model.addAttribute("dangnhapRequest", new DangkyRequest());
        return "login/Dangnhap";
    }
    @PostMapping("/Dangnhap")
    public String dangnhapPost(@Valid @ModelAttribute("dangnhapRequest") DangnhapRequest dangnhapRequest ,BindingResult result ,Model model) {
        if(result.hasErrors()){
            return "login/Dangnhap";
        }
        try {
            accoutService.Dangnhap(dangnhapRequest.getUsername(), dangnhapRequest.getPassword());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("dangnhapRequest", dangnhapRequest);
            return "login/Dangnhap";
        }
        
        return "home/trangchu";
    }

    @GetMapping("/Dangky")
    public String Dangky(Model model) {
        model.addAttribute("dangkyaccout", new DangkyRequest());
        return "login/Dangky";
    }
    @PostMapping("/Dangky")
    public String DangkyPost(@Valid @ModelAttribute("dangkyaccout") DangkyRequest dangkyRequest ,BindingResult result ,HttpSession session ,Model model){
        if(result.hasErrors()){
            return "login/Dangky";
        }
        try {
            Account account = accoutService.Dangky(dangkyRequest);
            session.setAttribute("account", account);
            String to = sendEmailConfig.generateOtp();
            String link = "http://localhost:8080/checkotp";
            String obj = "Xác nhận mã OTP để kích hoạt tài khoản" +link;
            String text = "mã OTP của bạn là  " + to; 
            sendEmailConfig.sendEmail(account.getEmail(), obj ,text );
            session.setAttribute("otp", to);
            return "login/otp";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "login/Dangky";
        }
    }
    @GetMapping("/thanhtoan")
    public String thanhtoan() {
        return "home/thanhtoan";
    }

    @GetMapping("/chitiet")
    public String chitiet() {
        return "home/chitiet";
    }

    @GetMapping("/giohang")
    public String getMethodName() {
        return "home/giohang";
    }

    @GetMapping("/profile")
    public String profile() {
        return "home/profile";
    }

    @GetMapping("/donmua")
    public String donmua() {
        return "home/donmua";
    }

    @GetMapping("/donhang")
    public String donhang() {
        return "home/donhang";
    }
    @GetMapping("/chitietdonhang")
    public String ctdonhang() {
        return "home/chitietdonhang";
    }
    @GetMapping("/checkotp" )
    public String check() {

        return "login/otp";
    }
    @PostMapping("/checkotp")
        public String checkotp(@RequestParam("otpInput") int  otpinput ,HttpSession session,Model model){
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
    @GetMapping("/test")
    public String testt(){
        return "";
    }
}