package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand,Integer> {
}
