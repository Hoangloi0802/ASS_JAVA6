package ass.java6.ass.Entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "vouchers")
public class Voucher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "ma_voucher", nullable = false, length = 50)
    private String maVoucher;
    
    @Column(name = "gia_tri_giam", nullable = false)
    private Double giaTriGiam;
    
    @Column(name = "don_toi_thieu", nullable = false)
    private Double donToiThieu;
    
    @Column(name = "ngay_het_han", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ngayHetHan;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "trang_thai")
    private Boolean trangThai = true;
}