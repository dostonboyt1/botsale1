package uz.pdp.botsale.payload;

import lombok.Data;
import uz.pdp.botsale.entity.ProductSize;

import java.util.List;
import java.util.UUID;

@Data
public class ResProductWithAmount {
    private UUID id;
    private ResProduct resProduct;
    private List<ResProduct> productListByCategoryAndBrand;
    private ProductSize productSize;
    private Integer amount;
}
