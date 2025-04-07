package ass.java6.ass.Service;

import ass.java6.ass.Entity.Account;

import ass.java6.ass.Entity.Product;
import ass.java6.ass.Entity.Order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartService {
    Order addToCart(Account account, Product product, int quantity);

    Order getCurrentCart(Account account);

    void updateQuantity(Account account, Product product, int quantity);

    void removeFromCart(Account account, Product product);

    int getTotalItemsInCart(Account account);

    double calculateTotalPrice(Account account);

    double calculateSubtotal(Account account);

    double calculateDiscount(Account account);

    void clearCart(Account account);

    void saveCart(Order cart);

    String getUsedVoucherCode(Account account);

    double tongthanhtoan(Account account);

    Order createOrderFromCart(Account account, String address);

    Order getOrderById(Long orderId);

    List<Order> getOrdersByUsername(String username);

    Order createTemporaryOrder(Account account, String address);

    void saveOrder(Order order);

    void updateOrder(Order order);

    // Thêm phương thức mới để xác nhận đã nhận hàng
    void confirmOrderReceived(Long orderId);

    void setUsedVoucherCode(Account account, String voucherCode);

    void updateOrderStatus(Long orderId, boolean isPaid);

    Page<Order> getOrdersByUsernamePaginated(String username, Pageable pageable);

    double calculateSubtotal(Order order);

    double calculateDiscount(Order order);

    double calculateOrderTotal(Order order);
}
