package ckb.BattleManager.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Battle")
@Table(name = "Battles", uniqueConstraints = {
        @UniqueConstraint(name = "repo_link_unique", columnNames = "repo_link")
})
public class Battle {
    @Id
    @Column(name = "battle_id", nullable = false, updatable = false)
    private Long battle_id;

    @Column(name = "tournament_id", nullable = false, updatable = false)
    private Long tournament_id;

    @Column(name = "repo_link")
    private String repo_link;

    @Column(name = "min_students", nullable = false, updatable = false)
    private int min_students;

    @Column(name = "max_students", nullable = false, updatable = false)
    private int max_students;

    @Column(name = "registration_deadline", nullable = false, updatable = false, columnDefinition = "DATE")
    private Date reg_deadline;

    @Column(name = "submission_deadline", nullable = false, updatable = false, columnDefinition = "DATE")
    private Date sub_deadline;

    @Column(name = "battle_to_eval", nullable = false, updatable = false)
    private boolean battle_to_eval;

}
