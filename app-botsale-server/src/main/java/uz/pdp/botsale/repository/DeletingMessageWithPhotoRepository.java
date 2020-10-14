package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.DeletingMessage;
import uz.pdp.botsale.entity.DeletingMessageWithPhoto;

import java.util.List;
import java.util.UUID;

public interface DeletingMessageWithPhotoRepository extends JpaRepository<DeletingMessageWithPhoto, UUID> {
    List<DeletingMessageWithPhoto> findAllByChatId(Long chatId);
}
