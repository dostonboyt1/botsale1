package uz.pdp.botsale.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.botsale.entity.enums.Gender;
import uz.pdp.botsale.entity.template.AbsEntity;

import javax.persistence.Entity;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer extends AbsEntity {
    private String firsName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Integer chatId;
    private boolean active=true;
    private String lang;
    private String botState;
    private String tempGender;
    private Integer tempParentCategoryId;
    private Integer tempChildrenCategoryId;
    private Integer tempBrandId;
    private UUID tempProductId;
    private UUID tempProductSizeId;
    private Integer tempAmount;
    private Integer tempMaxAmount;
    private Integer page=0;
    private Integer size=5;
}
