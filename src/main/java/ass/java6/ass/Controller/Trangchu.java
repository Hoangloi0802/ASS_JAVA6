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

}
