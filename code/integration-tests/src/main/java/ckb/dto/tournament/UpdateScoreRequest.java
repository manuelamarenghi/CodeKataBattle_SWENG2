package ckb.dto.tournament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
@AllArgsConstructor
public class UpdateScoreRequest {
    private Long tournamentID;
    private HashMap<Long, Integer> scores;
}
