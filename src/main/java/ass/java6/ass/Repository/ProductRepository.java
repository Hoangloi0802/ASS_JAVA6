package ass.java6.ass.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ass.java6.ass.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
