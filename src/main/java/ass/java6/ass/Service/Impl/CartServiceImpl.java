package ass.java6.ass.Service.Impl;

import ass.java6.ass.Entity.Order;
import ass.java6.ass.Entity.OrderDetail;
import ass.java6.ass.Entity.Product;
import ass.java6.ass.Entity.Voucher;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Repository.OrderRepository;
import ass.java6.ass.Repository.OrderDetailRepository;
import ass.java6.ass.Repository.ProductRepository;
import ass.java6.ass.Service.CartService;
import ass.java6.ass.Service.VoucherService;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private VoucherService voucherService;

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

                    newOrder.setOrderDetails(new ArrayList<>()); // Initialize orderDetails

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

        // Tính tổng tiền thanh toán
        return Math.max(subtotal - discount, 0); // Đảm bảo không âm

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
        double ship = 50000; // Fixed duplicate declaration
        return Math.max(subtotal - discount + ship, 0);

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
    @Transactional
    public Order createOrderFromCart(Account account, String address) {
        Order cart = getCurrentCart(account);

        if (cart == null || cart.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng rỗng, không thể tạo đơn hàng.");
        }

        cart.setStatus("SHIPPING");
        cart.setCreateDate(LocalDateTime.now());
        cart.setAddress(address);

        // Update product quantities with transaction
        for (OrderDetail orderDetail : cart.getOrderDetails()) {
            Product product = productRepository.findById(orderDetail.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
            int orderedQuantity = orderDetail.getQuantity();

            if (product.getQuantity() < orderedQuantity) {
                throw new IllegalArgumentException("Sản phẩm " + product.getName() + " không đủ số lượng trong kho.");
            }
            product.setQuantity(product.getQuantity() - orderedQuantity);
            productRepository.save(product);
        }

        orderRepository.save(cart);

        // Create new cart
        Order newCart = new Order();
        newCart.setAccount(account);
        newCart.setStatus("CART");
        newCart.setOrderDetails(new ArrayList<>());
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
        order.setStatus("SHIPPING");
        order.setAccount(account);
        order.setCreateDate(LocalDateTime.now());
        order.setVoucher(cart.getVoucher()); // Chuyển voucher từ giỏ hàng sang đơn tạm

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



    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, boolean isPaid) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        // Assuming status should be String, not boolean
        order.setStatus(isPaid ? "PAID" : "UNPAID");
        orderRepository.save(order);
    }

    @Override
    public void setUsedVoucherCode(Account account, String voucherCode) {
        Order cart = getCurrentCart(account);
        if (cart != null && voucherCode != null && !voucherCode.trim().isEmpty()) {
            Voucher voucher = voucherService.findByCode(voucherCode);
            if (voucher != null) {
                cart.setVoucher(voucher);
                orderRepository.save(cart);
            }

        }
    }


    @Override
    public double calculateSubtotal(Order order) {
        if (order == null || order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            return 0.0;
        }
        return order.getOrderDetails().stream()
                .mapToDouble(item -> (item.getPrice() != null ? item.getPrice() : 0) *
                        (item.getQuantity() != null ? item.getQuantity() : 0))
                .sum();
    }

    @Override
    public double calculateDiscount(Order order) {
        return (order != null && order.getVoucher() != null) ? order.getVoucher().getDiscountAmount() : 0.0;
    }

    @Override
    public double calculateOrderTotal(Order order) {
        if (order == null) {
            return 0.0;
        }
        double subtotal = calculateSubtotal(order);
        double discount = calculateDiscount(order);
        double SHIPPING_FEE = 50000; // Phí vận chuyển cố định
        return Math.max(subtotal - discount + SHIPPING_FEE, 0);
    }

    public Page<Order> getOrdersByUsernamePaginated(String username, Pageable pageable) {
        List<String> excludedStatuses = List.of(Order.STATUS_PENDING, Order.STATUS_CART);
        return orderRepository.findByAccountUsernameAndStatusNotIn(username, excludedStatuses, pageable);
    }

    
}