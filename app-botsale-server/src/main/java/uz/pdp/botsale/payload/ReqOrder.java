package uz.pdp.botsale.payload;

import lombok.Data;
import uz.pdp.botsale.entity.enums.OrderStatus;
import uz.pdp.botsale.entity.enums.PayStatus;

import java.util.List;
import java.util.UUID;

@Data
public class ReqOrder {
    private Integer id;

    private UUID customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerPhoneNumber;
    private String customerAddress;

    private String orderAddress;

    private Integer payTypeId;

    private List<ReqProductWithAmount> reqProductWithAmountList;

    private OrderStatus orderStatus;

    private PayStatus payStatus;

}
