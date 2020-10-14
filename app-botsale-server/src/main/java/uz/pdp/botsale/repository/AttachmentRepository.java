package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.Attachment;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findAllByNameAndSize(String name, long size);
}
