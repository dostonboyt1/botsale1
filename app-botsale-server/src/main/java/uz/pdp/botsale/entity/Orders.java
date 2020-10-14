package uz.pdp.botsale.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import uz.pdp.botsale.entity.enums.OrderStatus;
import uz.pdp.botsale.entity.enums.PayStatus;
import uz.pdp.botsale.entity.template.AbsEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OrderBy
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private Double lan;
    private Double lat;
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private PayType payType;

    private double orderSum;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "orders",cascade = CascadeType.ALL)
    private List<ProductWithAmount> pRoductWithAmountList;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse warehouse;

    private boolean fromBot;

}
