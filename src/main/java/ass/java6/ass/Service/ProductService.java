package ass.java6.ass.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import ass.java6.ass.Entity.OrderDetail;
import ass.java6.ass.Entity.Product;

public interface ProductService {
    List<Product> findAll();

    Page<Product> filterSortAndPaginate(String keyword, Double priceFilter, String categoryId, String sort,
            Pageable pageable);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    List<Product> findTopSellingProducts(int limit);

    List<Product> findLatestProducts(int limit);

    Product add(Product product);

    void deleteById(Integer id);

    boolean decreaseStockForOrder(List<OrderDetail> orderDetails);
    boolean restoreStockForOrder(List<OrderDetail> orderDetails) ;
    Product save(Product product);

    boolean decreaseStock(Integer productId, int quantity);

    Optional<Product> findById1(Integer id);

    Product update(Product product, MultipartFile mainImageFile, List<MultipartFile> additionalImageFiles);
}