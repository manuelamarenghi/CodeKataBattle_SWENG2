package ckb.TournamentManager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Data
@Getter
@Setter
@Entity
@Table(name= "tournaments")

public class Tournament {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column( name = "tournamentID",nullable = false, updatable = false)
    private Long tournamentID;
    @Column(name= "regdeadline")
    private Date regdeadline;

    @Column(name= "status")
    private Boolean status;

    @Column(name = "creatorID")
    private Long creatorID;
    public Tournament() {

    }

}
