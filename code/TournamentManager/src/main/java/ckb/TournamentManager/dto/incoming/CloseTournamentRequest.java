package ckb.TournamentManager.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CloseTournamentRequest {
    private Long tournamentID;
    private Long creatorID;
}
