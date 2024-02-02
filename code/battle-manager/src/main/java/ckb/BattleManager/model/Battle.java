package ckb.BattleManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Battle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long battleId;

    @Column(unique = true)
    private String name;

    private Long tournamentId;

    private String repositoryLink;

    @OneToMany(mappedBy = "battle", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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
