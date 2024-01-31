package ckb.TournamentManager.dto.outcoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBattlesRequest {
    private Long battleId;
}
