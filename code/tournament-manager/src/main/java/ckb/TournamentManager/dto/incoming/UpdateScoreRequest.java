package ckb.TournamentManager.dto.incoming;

import ckb.TournamentManager.model.WorkingPair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UpdateScoreRequest {
    private Long tournamentID;
    private List<WorkingPair<Long, Integer>> scores;
}
