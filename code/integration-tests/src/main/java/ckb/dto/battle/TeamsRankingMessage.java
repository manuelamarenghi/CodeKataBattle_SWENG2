package ckb.dto.battle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamsRankingMessage {
    private List<Pair<Long, Integer>> listTeamsIdScore;
}
