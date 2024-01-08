package ckb.BattleManager.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Participation")
@Table(name = "Participations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Participation {

    @EmbeddedId
    @Column(nullable = false, updatable = false)
    private ParticipationId participationId;
}
