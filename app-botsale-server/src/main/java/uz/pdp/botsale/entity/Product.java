package uz.pdp.botsale.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.botsale.entity.enums.Gender;
import uz.pdp.botsale.entity.template.AbsEntity;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product extends AbsEntity {
    private String nameUz;
    private String nameRu;
    private String descriptionUz;
    private String descriptionRu;
    private double incomePrice;
    private double salePrice;
    private Integer norm;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Attachment attachment;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private Brand brand;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_productsize", joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "productsize_id")})
    private List<ProductSize> productSizeList;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private boolean active;

}
