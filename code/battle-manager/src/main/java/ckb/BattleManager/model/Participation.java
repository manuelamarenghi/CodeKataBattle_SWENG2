package ckb.BattleManager.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Participation")
@Table(name = "Participations")
@Data
public class Participation {
    @EmbeddedId
    private ParticipationId participationId;

}
