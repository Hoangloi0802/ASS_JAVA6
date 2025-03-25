package ass.java6.ass.Service.Impl;

import ass.java6.ass.Entity.*;
import ass.java6.ass.Repository.*;
import ass.java6.ass.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private OrderDetailRepository orderDetailRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private AccountRepository accountRepository;

        // Thêm sản phẩm vào giỏ hàng
        @Override
        public void addToCart(String username, Long id, int quantity) {
                Account account = accountRepository.findById(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                // Kiểm tra xem user có giỏ hàng chưa thanh toán không
                Order order = orderRepository.findByAccountAndStatus(account, false)
                                .orElseGet(() -> {
                                        Order newOrder = new Order();
                                        newOrder.setAccount(account);
                                        newOrder.setStatus(false); // Chưa thanh toán
                                        return orderRepository.save(newOrder);
                                });

                // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
                Optional<OrderDetail> existingItem = orderDetailRepository.findByOrderAndProduct(order, product);
                if (existingItem.isPresent()) {
                        OrderDetail orderDetail = existingItem.get();
                        orderDetail.setQuantity(orderDetail.getQuantity() + quantity);
                        orderDetailRepository.save(orderDetail);
                } else {
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrder(order);
                        orderDetail.setProduct(product);
                        orderDetail.setQuantity(quantity);
                        orderDetail.setPrice(product.getPrice());
                        orderDetailRepository.save(orderDetail);
                }
        }

        // Lấy danh sách sản phẩm trong giỏ hàng
        @Override
        public List<OrderDetail> getCartItems(String username ) {
                Account account = accountRepository.findById(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Order order = orderRepository.findByAccountAndStatus(account, false)
                                .orElse(null);
                if (order != null) {
                        return orderDetailRepository.findByOrder(order);
                }
                return List.of(); // Trả về danh sách rỗng nếu không có giỏ hàng
        }

        // Xóa sản phẩm khỏi giỏ hàng
        @Override
        public void removeFromCart(String username, Long id) {
                Account account = accountRepository.findById(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                Order order = orderRepository.findByAccountAndStatus(account, false)
                                .orElse(null);
                if (order != null) {
                        Optional<OrderDetail> orderDetail = orderDetailRepository.findByOrderAndProduct(order, product);
                        orderDetail.ifPresent(orderDetailRepository::delete);
                }
        }

        // Xóa toàn bộ giỏ hàng
        @Override
        public void clearCart(String username) {
                Account account = accountRepository.findById(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Order order = orderRepository.findByAccountAndStatus(account, false)
                                .orElse(null);
                if (order != null) {
                        orderDetailRepository.deleteAll(orderDetailRepository.findByOrder(order));
                }
        }
        @Override
        public void updateQuantity(String username, Long id, int quantity) {
            Account account = accountRepository.findById(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
        
            Order order = orderRepository.findByAccountAndStatus(account, false)
                    .orElse(null);
            if (order != null) {
                Optional<OrderDetail> orderDetail = orderDetailRepository.findByOrderAndProduct(order, product);
                orderDetail.ifPresent(orderDetail1 -> {
                    orderDetail1.setQuantity(quantity);
                    orderDetailRepository.save(orderDetail1); // ✅ Lưu thay đổi vào database
                });
            }
        }
        public Optional<OrderDetail> findItemInCart(String username, Long productId) {
                Account account = accountRepository.findById(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));
            
                Order order = orderRepository.findByAccountAndStatus(account, false)
                        .orElse(null);
            
                if (order != null) {
                    return orderDetailRepository.findByOrderAndProduct(order, product);
                }
                return Optional.empty();
            }
            
}
