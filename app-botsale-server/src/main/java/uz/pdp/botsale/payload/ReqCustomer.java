package uz.pdp.botsale.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class ReqCustomer {
    private UUID id;
    private String firsName;
    private String lastName;
    private String phoneNumber;
    private String address;
}
