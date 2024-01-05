package ckb.TournamentManager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Entity
@Setter
@Getter
@Table(name= "tournamentrankings")
@IdClass(TournamentRankingId.class)

public class TournamentRanking implements Serializable {
    @Id
    @Column(name= "tournamentID")
    private Long tournamentID;
    @Id
    @Column(name= "userID")
    private Long userID;

    @Column(name= "score")
    private int score;

    public TournamentRanking() {

    }

}
