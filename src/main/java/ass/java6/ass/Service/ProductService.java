package ass.java6.ass.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ass.java6.ass.Entity.Product;

public interface ProductService {
    List<Product> findAll();
    Product findById(Integer id);
    Page<Product> filterSortAndPaginate(String keyword, Double priceFilter, String categoryId, String sort, Pageable pageable);
    List<Product> findTopSellingProducts(int limit);
    List<Product> findLatestProducts(int limit);
    Product add(Product product);
    List<Product> Danhsach();
}
