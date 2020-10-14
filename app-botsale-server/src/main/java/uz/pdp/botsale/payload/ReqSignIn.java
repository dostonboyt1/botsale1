package uz.pdp.botsale.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReqSignIn {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;
}
