package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhoneNumber(String phoneNumber);

    void deleteById(UUID uuid);
}
