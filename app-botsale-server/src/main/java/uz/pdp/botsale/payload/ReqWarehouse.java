package uz.pdp.botsale.payload;

import lombok.Data;
import uz.pdp.botsale.entity.enums.WarehouseStatus;

import java.util.UUID;

@Data
public class ReqWarehouse {
    private Integer id;
    private String name;
    private String address;
    private Integer districtId;
    private UUID userId;
    private WarehouseStatus warehouseStatus;
}
