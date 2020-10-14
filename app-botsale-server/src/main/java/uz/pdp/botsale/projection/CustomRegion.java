package uz.pdp.botsale.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.botsale.entity.Region;

@Projection(name = "customRegion", types = {Region.class})
public interface CustomRegion {
    Integer getId();
    String getName();

}
