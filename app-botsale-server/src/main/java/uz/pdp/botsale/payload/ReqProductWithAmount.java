package uz.pdp.botsale.payload;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReqProductWithAmount {
    private UUID id;
    private UUID productId;
    private UUID size;
    private Integer amount;
    private Integer canceledAmount;
}
