package ass.java6.ass.Service.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ass.java6.ass.Entity.Product;
import ass.java6.ass.Entity.ProductImage;
import ass.java6.ass.Repository.ProductRepository;
import ass.java6.ass.Service.FileUploadService;
import ass.java6.ass.Service.ProductService;

@Service
public class ProductImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> filterSortAndPaginate(String keyword, Double priceFilter, String categoryId, String sort,
            Pageable pageable) {
        Specification<Product> spec = Specification.where(null);
        String keywordPattern = (keyword != null && !keyword.trim().isEmpty())
                ? "%" + keyword.trim().toLowerCase() + "%"
                : null;

        if (priceFilter != null && priceFilter > 0) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("price"), priceFilter));
        }

        if (categoryId != null && !categoryId.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }

        if (keywordPattern != null) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), keywordPattern));
        }

        return productRepository.findAll(spec, pageable);
    }

    @Override
    public List<Product> findTopSellingProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return ((Slice<Product>) productRepository.findTopSellingProducts(pageable)).getContent();
    }

    @Override
    public List<Product> findLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return ((Slice<Product>) productRepository.findLatestProducts(pageable)).getContent();
    }

    @Override
    @Transactional
    public Product add(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById1(Integer id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Hibernate.initialize(product.getCategory());
            Hibernate.initialize(product.getProductImages());
        }
        return productOptional;
    }

    @Override
    @Transactional
    public Product update(Product product, MultipartFile mainImageFile, List<MultipartFile> additionalImageFiles) {
        Optional<Product> existingProductOpt = productRepository.findById(product.getId());
        if (!existingProductOpt.isPresent()) {
            throw new RuntimeException("Product with ID " + product.getId() + " not found");
        }

        Product updatedProduct = existingProductOpt.get();

        updatedProduct.setName(product.getName());
        updatedProduct.setPrice(product.getPrice());
        updatedProduct.setDescription(product.getDescription());
        updatedProduct.setQuantity(product.getQuantity());
        updatedProduct.setAvailable(product.getAvailable());
        updatedProduct.setCategory(product.getCategory());

        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            try {
                if (updatedProduct.getImage() != null && !updatedProduct.getImage().isEmpty()) {
                    fileUploadService.deleteFile(updatedProduct.getImage());
                }
                String imagePath = fileUploadService.uploadFile(mainImageFile, "products");
                updatedProduct.setImage(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to update main image: " + e.getMessage());
            }
        }

        if (additionalImageFiles != null && !additionalImageFiles.isEmpty()) {
            try {
                if (!updatedProduct.getProductImages().isEmpty()) {
                    for (ProductImage oldImage : updatedProduct.getProductImages()) {
                        fileUploadService.deleteFile(oldImage.getImageUrl());
                    }
                    updatedProduct.getProductImages().clear();
                }

                List<ProductImage> newProductImages = new ArrayList<>();
                for (MultipartFile file : additionalImageFiles) {
                    if (!file.isEmpty()) {
                        String imagePath = fileUploadService.uploadFile(file, "products");
                        ProductImage productImage = new ProductImage();
                        productImage.setImageUrl(imagePath);
                        productImage.setProduct(updatedProduct);
                        newProductImages.add(productImage);
                    }
                }
                updatedProduct.getProductImages().addAll(newProductImages);
            } catch (IOException e) {
                throw new RuntimeException("Failed to update additional images: " + e.getMessage());
            }
        }

        return productRepository.save(updatedProduct);
    }
}