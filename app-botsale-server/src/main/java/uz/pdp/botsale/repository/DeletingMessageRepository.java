package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.DeletingMessage;

import java.util.List;
import java.util.UUID;

public interface DeletingMessageRepository extends JpaRepository<DeletingMessage, UUID> {
    List<DeletingMessage> findAllByChatId(Long chatId);
}
