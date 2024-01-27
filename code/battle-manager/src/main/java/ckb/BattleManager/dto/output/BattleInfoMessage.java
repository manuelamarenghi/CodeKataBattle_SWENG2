package ckb.BattleManager.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@AllArgsConstructor
@Data
public class BattleInfoMessage {
    List<Pair<Long, Integer>> pairsIdTeamPoints;
}
