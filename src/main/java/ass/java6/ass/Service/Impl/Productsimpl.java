package ass.java6.ass.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ass.java6.ass.Entity.Product;
import ass.java6.ass.Repository.ProductRepository;
import ass.java6.ass.Service.ProductService;

public class Productsimpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

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
