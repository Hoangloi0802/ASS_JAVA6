package ass.java6.ass.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ass.java6.ass.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("FROM Product o WHERE o.name LIKE ?1")
    List<Product> findByKeywords(String keywords);

    @Query("SELECT od.product FROM OrderDetail od GROUP BY od.product ORDER BY SUM(od.quantity) DESC")
    Page<Product> findTopSellingProducts(Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.createDate DESC")
    Page<Product> findLatestProducts(Pageable pageable);
}