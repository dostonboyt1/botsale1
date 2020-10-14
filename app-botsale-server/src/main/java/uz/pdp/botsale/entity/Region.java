package uz.pdp.botsale.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uz.pdp.botsale.entity.template.AbsNameEntity;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Region extends AbsNameEntity {
}
