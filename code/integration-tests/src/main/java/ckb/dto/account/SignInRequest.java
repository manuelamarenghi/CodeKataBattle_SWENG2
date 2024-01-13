package ckb.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SignInRequest {
    private String email;
    private String password;
}
