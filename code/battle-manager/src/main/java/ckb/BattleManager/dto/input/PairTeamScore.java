package ckb.BattleManager.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PairTeamScore {
    private Long idTeam;
    private Integer score;
}
