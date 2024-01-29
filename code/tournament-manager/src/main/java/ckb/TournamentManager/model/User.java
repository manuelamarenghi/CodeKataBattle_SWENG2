package ckb.TournamentManager.model;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
    private long id;
    private String email;
    private String fullName;
    private String password;
    private Role role;
}
