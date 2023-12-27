package ckb.AccountManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SignUpRequest {
    private String email;
    private String fullName;
    private String password;
    private String role;
}
