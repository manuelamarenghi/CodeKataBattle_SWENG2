package ckb.TournamentManager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Setter
@Table(name= "TournamentRankings")

public class TournamentRanking {
    @Id
    @Column(name = "tournament_id", nullable = false, updatable = false)
    private Long tournamentID;
    @Column(name= "user_id")
    private Long userID;

    @Column(name= "score")
    private int score;

    public TournamentRanking() {

    }
    public Long getTournamentID() {
        return tournamentID;
    }
    public Long getUserID() {
        return userID;
    }
    public int getScore() {
        return score;
    }
}
