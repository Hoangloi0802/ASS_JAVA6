package ass.java6.ass.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ass.java6.ass.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
       @Query("FROM Product o WHERE o.name LIKE ?1")
       List<Product> findByKeywords(String keywords);

       @Query("SELECT p FROM Product p " +
                     "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(:keyword)) " +
                     "AND (:priceFilter IS NULL OR p.price <= :priceFilter) " +
                     "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
       Page<Product> filterProducts(
                     @Param("keyword") String keyword,
                     @Param("priceFilter") Double priceFilter,
                     @Param("categoryId") String categoryId,
                     Pageable pageable);

       @Query("SELECT p FROM Product p " +
                     "LEFT JOIN p.orderDetails od " +
                     "WHERE p.available = true AND p.quantity > 0 " +
                     "GROUP BY p " +
                     "ORDER BY COALESCE(SUM(od.quantity), 0) DESC")
       Slice<Product> findTopSellingProducts(Pageable pageable);

       @Query("SELECT p FROM Product p ORDER BY p.createDate DESC")
       Page<Product> findLatestProducts(Pageable pageable);

       @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
       List<Product> findByCategoryId(String categoryId);

       Page<Product> findAll(Specification<Product> spec, Pageable pageable);

       Optional<Product> findById(Long id);

       Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

}