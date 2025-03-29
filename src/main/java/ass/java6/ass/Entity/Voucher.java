package ass.java6.ass.Entity;

import java.io.Serializable;
import java.sql.Date;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "Vouchers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voucher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount;

    @Column(name = "min_order_value", nullable = false)  
    private Double minOrderValue;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;
}
