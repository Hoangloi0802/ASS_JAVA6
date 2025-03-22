package ass.java6.ass.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ass.java6.ass.Entity.Product;

public interface ProductService {
    List<Product> findAll();
    Product findById(Integer id);
    List<Product> searchByName(String keyword);
    
    Page<Product> filterSortAndPaginate(Double priceFilter, String categoryId, String sort, Pageable pageable);
}
