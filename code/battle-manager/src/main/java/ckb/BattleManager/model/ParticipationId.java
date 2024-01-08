package ckb.BattleManager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ParticipationId implements Serializable {
    @Column(insertable = false, updatable = false)
    private Long teamId;

    @ManyToOne
    @JoinColumn(name = "teamId")
    private Team studentId;
}
