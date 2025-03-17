package ass.java6.ass.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ass.java6.ass.Entity.Category;
import ass.java6.ass.Entity.Product;
import jakarta.servlet.http.HttpSession;

@Service
public class SessionService {
    @Autowired
    HttpSession session;

    /**
     * Đọc giá trị của attribute trong session
     * 
     * @param name tên attribute
     * @return giá trị đọc được hoặc null nếu không tồn tại
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name, T defaultValue) {
        T value = (T) session.getAttribute(name);
        return (value != null) ? value : defaultValue;
    }

    /**
     * Thay đổi hoặc tạo mới attribute trong session
     * 
     * @param name  tên attribute
     * @param value giá trị attribute
     */
    public void set(String name, Object value) {
        session.setAttribute(name, value);
    }

    /**
     * Xóa attribute trong session
     * 
     * @param name tên attribute cần xóa
     */
    public void remove(String name) {
        session.removeAttribute(name);
    }

    public List<Product> getCart() {
        return (List<Product>) session.getAttribute("cart");
    }

    public List<Category> getCategories() {
        return (List<Category>) session.getAttribute("categories");
    }

    public int getCartQuantity() {
        List<Product> cart = getCart();
        return cart == null ? 0 : cart.size();
    }

    public void setCart(List<Product> cart) {
        session.setAttribute("cart", cart);
    }

    public void setCategories(List<Category> categories) {
        session.setAttribute("categories", categories);
    }
}
