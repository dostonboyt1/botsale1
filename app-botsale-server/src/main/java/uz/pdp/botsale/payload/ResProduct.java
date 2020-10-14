package uz.pdp.botsale.payload;

import lombok.Data;
import uz.pdp.botsale.entity.Brand;
import uz.pdp.botsale.entity.ProductSize;
import uz.pdp.botsale.entity.enums.Gender;

import java.util.List;
import java.util.UUID;

@Data
public class ResProduct {
    private UUID id;
    private UUID photoId;
    private ResCategory resCategory;
    private Brand brand;
    private List<ProductSize> productSizeList;
    private String nameUz;
    private String nameRu;
    private String descriptionUz;
    private String descriptionRu;
    private double incomePrice;
    private double salePrice;
    private Integer norm;
    private Gender gender;

}
