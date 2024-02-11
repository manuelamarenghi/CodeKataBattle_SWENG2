package ckb.TournamentManager.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseTournamentRequest {
    private Long tournamentID;
    private Long creatorID;
}
