package ass.java6.ass.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ass.java6.ass.Entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

}
