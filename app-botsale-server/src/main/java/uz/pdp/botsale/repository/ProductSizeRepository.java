package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.Category;
import uz.pdp.botsale.entity.ProductSize;

import java.util.UUID;

public interface ProductSizeRepository extends JpaRepository<ProductSize, UUID> {
    boolean existsByName(String name);
}
