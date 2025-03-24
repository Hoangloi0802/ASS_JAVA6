package ass.java6.ass.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ass.java6.ass.Entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
   
}
