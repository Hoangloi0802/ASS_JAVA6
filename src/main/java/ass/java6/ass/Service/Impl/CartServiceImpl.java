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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public double calculateTotalPrice(Account account) {
        Order cart = getCurrentCart(account);
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            return 0.0;
        }

        // Tính tổng tiền sản phẩm trong giỏ hàng
        double subtotal = cart.getOrderDetails().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Áp dụng giảm giá từ voucher (nếu có)
        double discount = (cart.getVoucher() != null) ? cart.getVoucher().getDiscountAmount() : 0.0;

        // Tính tổng tiền thanh toán
        return Math.max(subtotal - discount, 0); // Đảm bảo không âm
    }

    public double tongthanhtoan(Account account) {
        Order cart = getCurrentCart(account);
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            return 0.0;
        }
        double subtotal = cart.getOrderDetails().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        double discount = (cart.getVoucher() != null) ? cart.getVoucher().getDiscountAmount() : 0.0;
        double ship = 50000;
        return Math.max(subtotal - discount - ship, 0); // Đảm bảo không âm
    }

    @Override
    public double calculateSubtotal(Account account) {
        Order cart = getCurrentCart(account);
        return cart.getOrderDetails().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public double calculateDiscount(Account account) {
        Order cart = getCurrentCart(account);
        return (cart.getVoucher() != null) ? cart.getVoucher().getDiscountAmount() : 0.0;
    }

    @Override
    public void saveCart(Order cart) {
        orderRepository.save(cart);
    }

    @Override
    public void clearCart(Account account) {
        Order cart = getCurrentCart(account);
        if (cart != null) {
            cart.getOrderDetails().clear();
            cart.setVoucher(null); // Xóa voucher nếu có
            orderRepository.save(cart);
        }
    }

    @Override
    public Order createOrderFromCart(Account account) {
        Order cart = getCurrentCart(account);

        if (cart == null || cart.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng rỗng, không thể tạo đơn hàng.");
        }

        // Đánh dấu đơn hàng đã thanh toán
        cart.setStatus(true);
        cart.setCreateDate(LocalDateTime.now()); // Lưu ngày đặt hàng

        // Lưu đơn hàng vào database
        orderRepository.save(cart);

        // Tạo giỏ hàng mới cho lần mua tiếp theo
        Order newCart = new Order();
        newCart.setAccount(account);
        newCart.setStatus(false); // Chưa thanh toán
        orderRepository.save(newCart);

        return cart;
    }

    @Override
    public String getUsedVoucherCode(Account account) {
        Order cart = getCurrentCart(account);
        if (cart == null || cart.getVoucher() == null) {
            return ""; // Không có voucher nào được sử dụng
        }
        return cart.getVoucher().getCode();
    }

    @Override
    public Order createOrderFromCart(Account account, String address) {
        Order cart = getCurrentCart(account);
    
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng rỗng, không thể tạo đơn hàng.");
        }
    
        // Đánh dấu đơn hàng đã thanh toán
        cart.setStatus(true);
        cart.setCreateDate(LocalDateTime.now()); // Lưu ngày đặt hàng
        cart.setAddress(address); // Lưu địa chỉ giao hàng
    
        // Giảm số lượng sản phẩm trong kho
        for (OrderDetail orderDetail : cart.getOrderDetails()) {
            Product product = orderDetail.getProduct();
            int orderedQuantity = orderDetail.getQuantity();
    
            // Kiểm tra nếu số lượng còn đủ để bán
            if (product.getQuantity() < orderedQuantity) {
                throw new IllegalArgumentException("Sản phẩm " + product.getName() + " không đủ số lượng trong kho.");
            }
    
            // Giảm số lượng sản phẩm
            product.setQuantity(product.getQuantity() - orderedQuantity);
            productRepository.save(product); // Lưu cập nhật vào database
        }
    
        // Lưu đơn hàng vào database
        orderRepository.save(cart);
    
        // Tạo giỏ hàng mới cho lần mua tiếp theo
        Order newCart = new Order();
        newCart.setAccount(account);
        newCart.setStatus(false); // Chưa thanh toán
        orderRepository.save(newCart);
    
        return cart;
    }
    
    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByAccount_Username(username);
    }

}
