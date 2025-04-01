package ass.java6.ass.Entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    private Integer quantity;
    @Temporal(TemporalType.DATE)
    @Column(name = "CreateDate")
    private Date createDate = new Date();
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "CategoryId", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProductImage> productImages;

    public String getFirstImage() {
        if (productImages != null && !productImages.isEmpty()) {
            return productImages.get(0).getImageUrl();
        }
        return "/images/default.jpg";
    }

    @Override
    public String toString() {
        return "Product{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", image='" + image + '\'' +
               ", price=" + price +
               ", description='" + description + '\'' +
               ", quantity=" + quantity +
               ", createDate=" + createDate +
               ", available=" + available +
               ", categoryId='" + (category != null ? category.getId() : null) + '\'' +
               ", orderDetailsCount=" + (orderDetails != null ? orderDetails.size() : 0) +
               ", productImagesCount=" + (productImages != null ? productImages.size() : 0) +
               '}';
    }
    public int getSoldQuantity() {
        if (orderDetails == null) {
            return 0;
        }
        return orderDetails.stream().mapToInt(OrderDetail::getQuantity).sum();
    }
    
}