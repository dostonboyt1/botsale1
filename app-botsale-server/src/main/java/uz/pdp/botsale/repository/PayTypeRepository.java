package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.PayType;

import java.util.List;

public interface PayTypeRepository extends JpaRepository<PayType, Integer> {
    List<PayType> findAllByActive(boolean active);
}
