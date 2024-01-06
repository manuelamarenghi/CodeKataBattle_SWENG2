package ckb.BattleManager.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class ParticipationId implements Serializable {
    private Long teamId;
    private Long studentId;
}
