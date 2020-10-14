package uz.pdp.botsale.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.botsale.entity.template.AbsEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductWithAmount extends AbsEntity {
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Product product;

    private int amount;
    private int canceledAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductSize productSize;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Orders orders;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Output output;
}
