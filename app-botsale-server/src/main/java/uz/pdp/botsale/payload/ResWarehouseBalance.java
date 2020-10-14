package uz.pdp.botsale.payload;

import lombok.Data;

@Data
public class ResWarehouseBalance {
    private Integer warehouseId;
    private String warehouseName;
    private double warehouseBalance;
}
