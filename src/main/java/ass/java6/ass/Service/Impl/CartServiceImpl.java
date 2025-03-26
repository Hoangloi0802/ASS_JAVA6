package ass.java6.ass.Service.Impl;

import ass.java6.ass.Entity.Order;
import ass.java6.ass.Entity.OrderDetail;
import ass.java6.ass.Entity.Product;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Repository.OrderRepository;
import ass.java6.ass.Repository.OrderDetailRepository;
import ass.java6.ass.Repository.ProductRepository;
import ass.java6.ass.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Order addToCart(Account account, Product product, int quantity) {
        // Lấy giỏ hàng hiện tại
        Order order = getCurrentCart(account);

        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
        Optional<OrderDetail> existingDetail = order.getOrderDetails().stream()
                .filter(od -> od.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingDetail.isPresent()) {
            // Nếu sản phẩm đã tồn tại, tăng số lượng
            OrderDetail orderDetail = existingDetail.get();
            orderDetail.setQuantity(orderDetail.getQuantity() + quantity);
            orderDetailRepository.save(orderDetail); // Cập nhật số lượng trong DB
        } else {
            // Nếu sản phẩm chưa có, tạo mới
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setQuantity(quantity);

            order.getOrderDetails().add(orderDetail);
            orderDetailRepository.save(orderDetail); // Lưu mới vào DB
        }

        return orderRepository.save(order);
    }

    @Override
    public void updateQuantity(Account account, Product product, int quantity) {
        Order cart = getCurrentCart(account);

        cart.getOrderDetails().stream()
                .filter(od -> od.getProduct().getId().equals(product.getId()))
                .findFirst()
                .ifPresent(orderDetail -> {
                    orderDetail.setQuantity(quantity);
                    orderDetailRepository.save(orderDetail);
                });
    }

    @Override
    public void removeFromCart(Account account, Product product) {
        Order cart = getCurrentCart(account);

        cart.getOrderDetails().removeIf(od -> od.getProduct().getId().equals(product.getId()));
        orderRepository.save(cart);
    }

    @Override
    public Order getCurrentCart(Account account) {
        return orderRepository.findByAccountAndStatus(account, false)
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setAccount(account);
                    newOrder.setStatus(false); // false nghĩa là giỏ hàng chưa thanh toán
                    return orderRepository.save(newOrder);
                });
    }
    public int getTotalItemsInCart(Account account) {
        Order cart = getCurrentCart(account);
        return cart.getOrderDetails().stream().mapToInt(OrderDetail::getQuantity).sum();
    }
    
}
