package ckb.TournamentManager.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@Table(name= "permissions")
@AllArgsConstructor

public class Permission{
    @Id
    @Column(name = "tournamentID",nullable = false, updatable = false)
    private Long tournamentID;
    @Column(name= "userID")
    private Long userID;

    public Permission() {

    }
}
