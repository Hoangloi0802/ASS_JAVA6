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
import java.util.ArrayList;
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

    @Override
    public Order addToCart(Account account, Product product, int quantity) {
        Order order = getCurrentCart(account);
        Optional<OrderDetail> existingDetail = order.getOrderDetails().stream()
                .filter(od -> od.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingDetail.isPresent()) {
            OrderDetail orderDetail = existingDetail.get();
            orderDetail.setQuantity(orderDetail.getQuantity() + quantity);
            orderDetailRepository.save(orderDetail);
        } else {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setQuantity(quantity);
            order.getOrderDetails().add(orderDetail);
            orderDetailRepository.save(orderDetail);
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
        return orderRepository.findByAccountAndStatus(account, "CART")
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setAccount(account);
                    newOrder.setStatus("CART");
                    return orderRepository.save(newOrder);
                });
    }

    @Override
    public int getTotalItemsInCart(Account account) {
        Order cart = getCurrentCart(account);
        return cart.getOrderDetails().stream().mapToInt(OrderDetail::getQuantity).sum();
    }

    @Override
    public double calculateTotalPrice(Account account) {
        Order cart = getCurrentCart(account);
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            return 0.0;
        }
        double subtotal = cart.getOrderDetails().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        double discount = (cart.getVoucher() != null) ? cart.getVoucher().getDiscountAmount() : 0.0;
        return Math.max(subtotal - discount, 0);
    }

    @Override
    public double tongthanhtoan(Account account) {
        Order cart = getCurrentCart(account);
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            return 0.0;
        }
        double subtotal = cart.getOrderDetails().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        double discount = (cart.getVoucher() != null) ? cart.getVoucher().getDiscountAmount() : 0.0;
        double ship = 50000; // Đổi từ 500000 thành 50000 để hợp lý hơn với phí vận chuyển
        return Math.max(subtotal - discount + ship, 0); // Thêm phí vận chuyển vào tổng
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
            cart.setVoucher(null);
            orderRepository.save(cart);
        }
    }

    @Override
    public Order createOrderFromCart(Account account) {
        Order cart = getCurrentCart(account);
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng rỗng, không thể tạo đơn hàng.");
        }
        cart.setStatus("SHIPPING"); // Đổi từ PENDING thành SHIPPING sau khi thanh toán
        cart.setCreateDate(LocalDateTime.now());
        orderRepository.save(cart);

        Order newCart = new Order();
        newCart.setAccount(account);
        newCart.setStatus("CART");
        orderRepository.save(newCart);

        return cart;
    }

    @Override
    public String getUsedVoucherCode(Account account) {
        Order cart = getCurrentCart(account);
        if (cart == null || cart.getVoucher() == null) {
            return "";
        }
        return cart.getVoucher().getCode();
    }

    @Override
    public Order createOrderFromCart(Account account, String address) {
        Order cart = getCurrentCart(account);

        if (cart == null || cart.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng rỗng, không thể tạo đơn hàng.");
        }

        // Đánh dấu đơn hàng đã thanh toán và chuyển sang trạng thái SHIPPING
        cart.setStatus("SHIPPING"); // Đổi từ PENDING thành SHIPPING
        cart.setCreateDate(LocalDateTime.now());
        cart.setAddress(address);

        // Giảm số lượng sản phẩm trong kho
        for (OrderDetail orderDetail : cart.getOrderDetails()) {
            Product product = orderDetail.getProduct();
            int orderedQuantity = orderDetail.getQuantity();

            if (product.getQuantity() < orderedQuantity) {
                throw new IllegalArgumentException("Sản phẩm " + product.getName() + " không đủ số lượng trong kho.");
            }

            product.setQuantity(product.getQuantity() - orderedQuantity);
            productRepository.save(product);
        }

        // Lưu đơn hàng vào database
        Order savedOrder = orderRepository.save(cart);

        // Làm trống giỏ hàng cũ
        clearCart(account);

        // Tạo giỏ hàng mới cho lần mua tiếp theo
        Order newCart = new Order();
        newCart.setAccount(account);
        newCart.setStatus("CART");
        orderRepository.save(newCart);

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByAccount_Username(username);
    }

    @Override
    public Order createTemporaryOrder(Account account, String address) {
        Order cart = getCurrentCart(account);
        if (cart == null || cart.getOrderDetails().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        Order order = new Order();
        order.setAddress(address);
        order.setStatus("SHIPPING"); // Đổi từ PENDING thành SHIPPING
        order.setAccount(account);
        order.setCreateDate(LocalDateTime.now());

        List<OrderDetail> newOrderDetails = new ArrayList<>();
        for (OrderDetail cartDetail : cart.getOrderDetails()) {
            OrderDetail newDetail = new OrderDetail();
            newDetail.setProduct(cartDetail.getProduct());
            newDetail.setPrice(cartDetail.getPrice());
            newDetail.setQuantity(cartDetail.getQuantity());
            newDetail.setOrder(order);
            newOrderDetails.add(newDetail);
        }
        order.setOrderDetails(newOrderDetails);

        return order;
    }

    @Override
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void confirmOrderReceived(Long orderId) {
        Order order = getOrderById(orderId);
        if (order != null && "SHIPPING".equals(order.getStatus())) {
            order.setStatus("SUCCESS");
            updateOrder(order);
        }
    }
}