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
    public Page<Product> filterSortAndPaginate(String keyword, Double priceFilter, String categoryId, String sort,
            Pageable pageable) {
        List<Product> products = productRepository.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
        }

        if (priceFilter != null && priceFilter > 0) {
            products = products.stream()
                    .filter(p -> p.getPrice() <= priceFilter)
                    .collect(Collectors.toList());
        }

        if (categoryId != null && !categoryId.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("5")) {
                products.sort(Comparator.comparing(Product::getPrice));
            } else if (sort.equals("6")) {
                products.sort(Comparator.comparing(Product::getPrice).reversed());
            }
        }

        int total = products.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);
        List<Product> pageContent = products.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, total);
    }

    @Override
    public List<Product> findTopSellingProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findTopSellingProducts(pageable);
    }

    @Override
    public List<Product> findLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findLatestProducts(pageable);
    }
    @Override
    public Product add(Product product) {
       
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public List<Product> Danhsach() {
        productRepository.findAll();
        throw new UnsupportedOperationException("Unimplemented method 'Danhsach'");
    }
}
