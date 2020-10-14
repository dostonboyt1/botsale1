package uz.pdp.botsale.payload;

import lombok.Data;
import uz.pdp.botsale.entity.ProductSize;

@Data
public class ResAmount {
    private ResProduct resProduct;
    private ProductSize productSize;
    private Integer amount;
}
