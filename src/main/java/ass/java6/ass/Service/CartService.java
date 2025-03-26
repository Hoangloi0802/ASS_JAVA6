package ass.java6.ass.Service;

import ass.java6.ass.Entity.Product;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Order;


public interface CartService {
    Order addToCart(Account account, Product product, int quantity);
    Order getCurrentCart(Account account);
    void updateQuantity(Account account, Product product, int quantity);
    void removeFromCart(Account account, Product product);
    int getTotalItemsInCart(Account account);
    
}
