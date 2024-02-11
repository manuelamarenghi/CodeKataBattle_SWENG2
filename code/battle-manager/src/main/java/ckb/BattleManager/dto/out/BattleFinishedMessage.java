package ckb.BattleManager.dto.out;

import ckb.BattleManager.model.WorkingPair;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BattleFinishedMessage {
    private Long idTournament;
    private List<WorkingPair<Long, Integer>> pairsIdUserPoints;
}
