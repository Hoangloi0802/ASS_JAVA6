package ass.java6.ass.Service.Impl;

import java.util.List;


import org.springframework.stereotype.Service;

import ass.java6.ass.Entity.Category;
import ass.java6.ass.Repository.CategoryRepository;
import ass.java6.ass.Service.CategoryService;

@Service
public class CategoryImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void save(Category category) {
        categoryRepository.save(category);
    }
    @Override
    public void deleteById(String id) {
        categoryRepository.deleteById(id);
    }
     public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}

