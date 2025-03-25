package ass.java6.ass.Service;

import ass.java6.ass.Entity.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductImageService {

    List<ProductImage> findAll();

    Optional<ProductImage> findById(Integer id);

    List<ProductImage> findByProductId(Integer productId);

    ProductImage save(ProductImage productImage);

    void deleteById(Integer id);

    void deleteByProductId(Integer productId);
}