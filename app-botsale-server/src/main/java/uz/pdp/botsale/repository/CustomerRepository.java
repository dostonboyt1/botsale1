package uz.pdp.botsale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Page<Customer> findAllByActive(boolean active, Pageable pageable);
    Page<Customer> findAllByPhoneNumberContainingIgnoreCase(String phoneNumber, Pageable pageable);
    Optional<Customer> findByChatId(Integer chatId);
    Optional<Customer> findByPhoneNumberContaining(String phoneNumber);
}
