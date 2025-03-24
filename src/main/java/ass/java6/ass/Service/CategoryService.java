package ass.java6.ass.Service;

import java.util.List;

import ass.java6.ass.Entity.Category;

public interface CategoryService {
    void save(Category category);
    void deleteById(String id);
    List<Category> findAll();
}
   
