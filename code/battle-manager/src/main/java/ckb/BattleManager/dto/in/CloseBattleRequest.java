package ckb.BattleManager.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloseBattleRequest {
    private Long battleId;
    private Long educatorId;
}
