package ass.java6.ass.Service.Impl;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ass.java6.ass.Service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ass.java6.ass.Entity.Product;
import ass.java6.ass.Repository.ProductRepository;

@Service
public class ProductImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Product> filterSortAndPaginate(Double priceFilter, String categoryId, String sort, Pageable pageable) {
        // Lấy tất cả
        List<Product> products = productRepository.findAll();
    
        // Filter giá
        if (priceFilter != null && priceFilter > 0) {
            products = products.stream()
                    .filter(p -> p.getPrice() <= priceFilter)
                    .collect(Collectors.toList());
        }
    
        // Filter danh mục
        if (categoryId != null && !categoryId.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }
    
        // Sort
        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("5")) { // Tăng dần
                products.sort(Comparator.comparing(Product::getPrice));
            } else if (sort.equals("6")) { // Giảm dần
                products.sort(Comparator.comparing(Product::getPrice).reversed());
            }
        }
    
        // Phân trang thủ công
        int total = products.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<Product> pageContent = products.subList(start, end);
    
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, total);
    }
    
    @Override
    public List<Product> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        String lowerKeyword = keyword.toLowerCase();
        return productRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

}
  

