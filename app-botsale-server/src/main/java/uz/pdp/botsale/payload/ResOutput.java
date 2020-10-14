package uz.pdp.botsale.payload;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
public class ResOutput {
    private UUID id;
    private Timestamp createdAt;
    private ResWarehouse incomer;
    private ResWarehouse outputer;
    private boolean isIncome;
    private List<ResProductWithAmount> resProductWithAmountList;
    private boolean confirmed;
}
