package ckb.BattleManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity(name = "Team")
@Table(name = "Teams")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @ManyToOne
    private Battle battle;

    @OneToMany(mappedBy = "participationId.team")
    private Collection<Participation> participation;

    private String repositoryLink;

    private Integer score;

    private Boolean eduEvaluated;
}
