package uz.pdp.botsale.payload;

import lombok.Data;

@Data
public class ReqPassword {
    private String oldPassword;
    private String password;
    private String prePassword;
}
