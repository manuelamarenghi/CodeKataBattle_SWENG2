package ckb.TournamentManager.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@Table(name= "permissions")
@AllArgsConstructor
@IdClass(PermissionId.class)

public class Permission{
    @Id
    @Column(name = "tournamentID")
    private Long tournamentID;
    @Id
    @Column(name= "userID")
    private Long userID;

    public Permission() {

    }
}
