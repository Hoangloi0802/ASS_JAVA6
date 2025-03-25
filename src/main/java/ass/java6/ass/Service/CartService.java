package ass.java6.ass.Service;

import java.util.List;
import java.util.Optional;

import ass.java6.ass.Entity.OrderDetail;

public interface CartService {
    void addToCart(String username, Long id, int quantity);
    List<OrderDetail> getCartItems(String username);
    void removeFromCart(String username, Long id);
    void clearCart(String username);
    void updateQuantity(String username, Long id, int quantity);
    Optional<OrderDetail> findItemInCart(String username, Long productId);
}
