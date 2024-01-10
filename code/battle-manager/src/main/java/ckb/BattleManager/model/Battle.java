package ckb.BattleManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "Battle")
@Table(name = "Battles")
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

    private int minStudents;

    private int maxStudents;

    private LocalDateTime regDeadline;

    private LocalDateTime subDeadline;

    private Boolean battleToEval;

}
