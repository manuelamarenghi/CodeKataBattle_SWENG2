package ckb.TournamentManager.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name= "Permissions")

public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tournament_id", nullable = false, updatable = false)
    private Long tournament_id;
    @Column(name= "user_id")
    private Long user_id;

    public Permission() {

    }

    public Long getTournament_id() {
        return tournament_id;
    }

    public Long getUser_id() {
        return user_id;
    }
}
