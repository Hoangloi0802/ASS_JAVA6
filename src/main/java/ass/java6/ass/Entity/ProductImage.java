package ass.java6.ass.Entity;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productimages")
public class ProductImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ImageUrl", nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "ProductId", nullable = false) 
    private Product product;
}