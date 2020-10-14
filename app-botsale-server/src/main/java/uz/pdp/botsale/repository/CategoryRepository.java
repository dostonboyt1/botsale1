package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByParentId(Integer parent_id);
    List<Category> findAllByParentIsNull();
}
