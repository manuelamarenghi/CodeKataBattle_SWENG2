package ckb.BattleManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity(name = "Team")
@Table(name = "Teams")
@AllArgsConstructor
@NoArgsConstructor
public class Team {
    @Id
    @Column(name = "team_id", nullable = false, updatable = false)
    private Long team_id;

    @ManyToOne
    @JoinColumn(name = "battle_id")
    private Battle battle;

    @Column(name = "repo_link")
    private String repo_link;

    @Column(name = "score")
    private Integer score;

    @Column(name = "edu_evaluated")
    private boolean edu_evaluated;

}
