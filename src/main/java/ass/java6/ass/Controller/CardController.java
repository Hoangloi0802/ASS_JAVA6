package ass.java6.ass.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CardController {

    @GetMapping("/giohang")
    public String giohang() {
        return "home/giohang";
    }

    @GetMapping("/thanhtoan")
    public String thanhtoan() {
        return "home/thanhtoan";
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
}
