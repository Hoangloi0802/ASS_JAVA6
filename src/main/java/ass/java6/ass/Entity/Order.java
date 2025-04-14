package ass.java6.ass.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "Orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    // Giữ nguyên kiểu String nhưng thêm các hằng số để đảm bảo tính nhất quán
    @Column(name = "status")
    private String status = STATUS_PENDING; // Mặc định là PENDING

    // Các hằng số định nghĩa trạng thái
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SHIPPING = "SHIPPING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_CART = "CART";

    @Column(name = "createdate")
    private LocalDateTime createDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
@Column(name = "payment_method")
private String paymentMethod;
    public int getTotalQuantity() {
        return orderDetails.stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
    }
    
    public String getStatusDisplayName() {
        switch (this.status) {
            case STATUS_PENDING:
                return "Chờ xử lý";
            case STATUS_SHIPPING:
                return "Đang giao hàng";
            case STATUS_SUCCESS:
                return "Đã giao hàng";
            case STATUS_FAILED:
                return "Đã hủy";
            case STATUS_CART:
                return "Giỏ hàng";
            default:
                return this.status;
        }
    }
    public static boolean isValidStatus(String status) {
        return STATUS_PENDING.equals(status) ||
               STATUS_SHIPPING.equals(status) ||
               STATUS_SUCCESS.equals(status) ||
               STATUS_FAILED.equals(status) ||
               STATUS_CART.equals(status);
    }
}