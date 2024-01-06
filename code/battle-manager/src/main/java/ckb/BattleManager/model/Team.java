package ckb.BattleManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Team")
@Table(name = "Teams")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team {
    @Id
    @Column(name = "teamId", nullable = false, updatable = false)
    private Long teamId;

    @ManyToOne
    @JoinColumn(name = "battleId")
    private Battle battle;

    @Column(name = "repositoryLink")
    private String repositoryLink;

    @Column(name = "score")
    private Integer score;

    @Column(name = "eduEvaluated")
    private boolean eduEvaluated;

}
