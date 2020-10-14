package uz.pdp.botsale.payload;

import lombok.Data;

@Data
public class ResCategory {
    private Integer id;
    private String nameUz;
    private String nameRu;
    private Integer parentId;
    private String parentNameUz;
    private String parentNameRu;

}
