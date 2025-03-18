package ass.java6.ass.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Dto.DangnhapRequest;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Service.AccoutService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;


@Controller
@RequestMapping("/")
public class Trangchu {
    @Autowired
    private AccoutService accoutService;
    @GetMapping("/")
    public String getMethodName(Model model) {
        return "home/trangchu";
    }
    @GetMapping("/shop")
    public String shop(Model model) {
        return "home/sanpham";
    }
    @GetMapping("/Dangnhap")
    public String dangnhap(){
        return "login/Dangnhap";
    }
    @GetMapping("/Dangky")
    public String Dangky( ){
        return "testdulieu/Dangky";
    }
    @GetMapping("/Dangkytest")
    public String Dangkytest(Model model) {
    model.addAttribute("accoutDangky", new DangkyRequest()); 
    return "testdulieu/Dangkytest";
    }
    @PostMapping("/Dangkytest")
    public String dangkyPost(@Valid @ModelAttribute("accoutDangky") DangkyRequest dangkyRequest, 
                         BindingResult result, 
                         Model model ,HttpSession session) {
    if (result.hasErrors()) {
        return "testdulieu/Dangkytest";
    }
    try {
        Account newAccount = accoutService.Dangky(dangkyRequest);

        session.setAttribute("accout", newAccount);
        model.addAttribute("message", "Đăng ký thành công!");
         return "redirect:/Dangnhaptest";
        
    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        return "testdulieu/Dangkytest";
    }
   
    }
    @GetMapping("/Dangnhaptest")
    public String dangnhaptest(Model model){
        model.addAttribute("accoutDangnhap", new DangnhapRequest());
        return "testdulieu/Dangnhaptest";
    }
    @PostMapping("Dangnhaptest")
    public String dangnhapPost(Model model ,@Valid @ModelAttribute("accoutDangnhap") DangnhapRequest dangnhapRequest ,BindingResult result ,HttpSession session){
        if(result.hasErrors()){
            return "testdulieu/Dangnhaptest";
        }
        try {
            Account account = accoutService.Dangnhap(dangnhapRequest.getUsername(), dangnhapRequest.getPassword());
            session.setAttribute("accout", account);
            model.addAttribute("message", "Đăng nhập thành công!");
            System.out.println("đăng nhập thành công");

        } catch (Exception e) {
            model.addAttribute("message", "Sai tài khoản hoặc mật khẩu!");
            return "testdulieu/Dangnhaptest";
        }
        

        return "testdulieu/Dangnhaptest";
    }




   
    
}
