package uz.pdp.botsale.payload;

import lombok.Data;

@Data
public class ReqDistrict {
    private Integer id;
    private String nameUz;
    private String nameRu;
    private Integer regionId;
}
