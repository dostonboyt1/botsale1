package uz.pdp.botsale.payload;

import lombok.Data;
import uz.pdp.botsale.entity.Customer;
import uz.pdp.botsale.entity.PayType;
import uz.pdp.botsale.entity.enums.OrderStatus;
import uz.pdp.botsale.entity.enums.PayStatus;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ResOrder {
    private Integer id;
    private Timestamp createdAt;
    private Customer customer;
    private String orderAddress;
    private Double lan;
    private Double lat;
    private PayType payType;
    private double orderSum;
    private List<ResProductWithAmount> resProductWithAmountList;
    private OrderStatus orderStatus;
    private PayStatus payStatus;
    private ResUser resUser;
    private ResWarehouse resWarehouse;
    private boolean fromBot;
}
