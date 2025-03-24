package ass.java6.ass.Entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String image;
    private Double price;
    private String description;
    @Temporal(TemporalType.DATE)
    @Column(name = "CreateDate")
    private Date createDate = new Date();
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "CategoryId", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> productImages;

    @Transient  // Không lưu vào DB, chỉ dùng cho View
    public String getFirstImage() {
        if (productImages != null && !productImages.isEmpty()) {
            return productImages.get(0).getImageUrl();  // Lấy ảnh đầu tiên
        }
        return "/img/no-image.jpg"; // Nếu không có ảnh, dùng ảnh mặc định
    }
}
