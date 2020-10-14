package uz.pdp.botsale.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.botsale.entity.District;
import uz.pdp.botsale.entity.Region;

@Projection(name = "customDistrict", types = {District.class})
public interface CustomDistrict {
    Integer getId();

    String getName();

    Region getRegion();
}
