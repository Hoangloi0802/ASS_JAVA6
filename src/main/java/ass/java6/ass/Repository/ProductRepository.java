package ass.java6.ass.Repository;

import java.util.List;

import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ass.java6.ass.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("FROM Product o WHERE o.name LIKE ?1")
    List<Product> findByKeywords(String keywords);
}
