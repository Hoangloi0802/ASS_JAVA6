package ass.java6.ass.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/gia")
public class CartController {

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam("quantity") Integer quantity,
                            HttpSession session, Model model) {

        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("CART");
        if (cart == null) {
            cart = new HashMap<>();
        }

        cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
        session.setAttribute("CART", cart);
        model.addAttribute("message", "Added to cart successfully!");
        return "redirect:/product/{id}"; 
    }

    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("CART");
        if (cart == null) {
            cart = new HashMap<>();
        }
        model.addAttribute("cart", cart);
        return ""; 
    }
}