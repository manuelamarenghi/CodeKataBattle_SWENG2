package ckb.BattleManager.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformStudentsRequest {
    private Long tournamentId;
    private String battleName;
}
