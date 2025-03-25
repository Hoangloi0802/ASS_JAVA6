package ass.java6.ass.Service.Impl;

import ass.java6.ass.Entity.ProductImage;
import ass.java6.ass.Repository.ProductImageRepository;
import ass.java6.ass.Service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public List<ProductImage> findAll() {
        return productImageRepository.findAll();
    }

    @Override
    public Optional<ProductImage> findById(Integer id) {
        return productImageRepository.findById(id);
    }

    @Override
    public List<ProductImage> findByProductId(Integer productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Override
    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    @Override
    public void deleteById(Integer id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public void deleteByProductId(Integer productId) {
        productImageRepository.deleteByProductId(productId);
    }
}