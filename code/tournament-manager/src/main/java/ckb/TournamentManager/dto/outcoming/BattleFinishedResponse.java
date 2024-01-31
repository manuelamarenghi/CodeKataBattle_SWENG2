package ckb.TournamentManager.dto.outcoming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mockserver.model.BodyWithContentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleFinishedResponse {
    private Boolean ableToClose;
}
