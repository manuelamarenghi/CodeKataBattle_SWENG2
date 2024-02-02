package ckb.dto.battle;

import ckb.model.WorkingPair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamsRankingMessage {
    private List<WorkingPair<Long, Integer>> listTeamsIdScore;
}
