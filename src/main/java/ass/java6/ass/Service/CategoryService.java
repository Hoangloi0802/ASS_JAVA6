package ass.java6.ass.Service;

import ass.java6.ass.Entity.Category;

public interface CategoryService {
    void save(Category category);
    void deleteById(String id);
}
