package uz.pdp.botsale.payload;

import lombok.Data;
import uz.pdp.botsale.entity.enums.WarehouseStatus;

@Data
public class ResWarehouse {
    private Integer id;
    private String name;
    private String address;
    private Integer districtId;
    private Integer regionId;
    private String districtName;
    private String regionName;
    private ResUser resUser;
    private WarehouseStatus warehouseStatus;
}
