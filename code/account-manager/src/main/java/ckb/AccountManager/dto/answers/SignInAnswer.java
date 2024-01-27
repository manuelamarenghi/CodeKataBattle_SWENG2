package ckb.AccountManager.dto.answers;


import ckb.AccountManager.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInAnswer {
    public String userID;
    public String email;
    public Role role;
}
