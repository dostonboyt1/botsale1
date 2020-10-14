package uz.pdp.botsale.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.botsale.entity.AttachmentType;

import java.util.UUID;

@Projection(name = "customAttachmentType", types = AttachmentType.class)
public interface CustomAttachmentType {
    UUID getId();
    String getContentTypes();
    Integer getWidth();
    Integer getHeight();
    String getType();
}
