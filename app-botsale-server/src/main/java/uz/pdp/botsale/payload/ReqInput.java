package uz.pdp.botsale.payload;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReqInput {
    private UUID id;
    private List<ReqProductWithAmount> reqProductWithAmountList;
    private Integer incomerWarehouseId;
}
