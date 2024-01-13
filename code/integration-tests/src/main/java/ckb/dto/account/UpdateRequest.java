package ckb.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {
    private Long id;
    private String email;
    private String fullName;
    private String password;
    private Role role;
}
