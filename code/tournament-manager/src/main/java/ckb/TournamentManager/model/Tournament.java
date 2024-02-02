package ckb.TournamentManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name= "tournaments")

public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tournamentID", nullable = false, updatable = false)
    private Long tournamentID;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name= "regdeadline")
    private Date regdeadline;

    @Column(name= "status")
    private Boolean status;

    @Column(name = "creatorID")
    private Long creatorID;

}
