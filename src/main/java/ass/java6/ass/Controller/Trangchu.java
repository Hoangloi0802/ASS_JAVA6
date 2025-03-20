package ass.java6.ass.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class Trangchu {
    @GetMapping("/")
    public String trangchu(Model model, HttpSession session) {
        return "home/trangchu";
    }

    @GetMapping("/shop")
    public String shop(Model model) {
        return "home/sanpham";
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

}