package ckb.BattleManager.dto.output;

import ckb.BattleManager.model.WorkingPair;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class BattleInfoMessage {
    List<WorkingPair<Long, Integer>> pairsIdTeamPoints;
}
