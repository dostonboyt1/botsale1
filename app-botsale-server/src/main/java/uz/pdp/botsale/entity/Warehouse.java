package uz.pdp.botsale.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.botsale.entity.enums.WarehouseStatus;
import uz.pdp.botsale.entity.template.AbsNameEntity;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Warehouse extends AbsNameEntity {
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private District district;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private WarehouseStatus warehouseStatus;

    private boolean active = true;
}
