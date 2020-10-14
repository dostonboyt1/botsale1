package uz.pdp.botsale.payload;

import lombok.Data;
import uz.pdp.botsale.entity.enums.Gender;

import java.util.List;
import java.util.UUID;

@Data
public class ReqProduct {
    private UUID id;
    private UUID photoId;
    private String nameUz;
    private String nameRu;
    private String descriptionUz;
    private String descriptionRu;
    private double incomePrice;
    private double salePrice;
    private Integer norm;
    private List<UUID> productSizeList;
    private Integer categoryId;
    private Integer brandId;
    private Gender gender;
}
