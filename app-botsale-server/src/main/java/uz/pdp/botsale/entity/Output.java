package uz.pdp.botsale.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.botsale.entity.template.AbsEntity;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Output extends AbsEntity {
    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "orders",cascade = CascadeType.ALL)
    private List<ProductWithAmount> pRoductWithAmountList;

    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse incomer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse outputer;

    private boolean confirmed;
}
