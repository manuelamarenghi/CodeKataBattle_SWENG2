package ckb.BattleManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Battle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long battleId;

    private Long tournamentId;

    @Column(unique = true)
    private String repositoryLink;

    @OneToMany(mappedBy = "battle")
    private List<Team> teamsRegistered;

    private int minStudents;

    private int maxStudents;

    private LocalDateTime regDeadline;

    private LocalDateTime subDeadline;

    private Boolean battleToEval;

    private Long authorId;

    private Boolean hasStarted;

    private Boolean hasEnded;

    private Boolean isClosed;
}
