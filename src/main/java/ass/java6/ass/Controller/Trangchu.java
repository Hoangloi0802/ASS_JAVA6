package ass.java6.ass.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;


@Controller
public class Trangchu {
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
        return "home/Dangnhap";
    }
    @GetMapping("/Dangky")
    public String Dangky(){
        return "home/Dangky";
    }
    
}
