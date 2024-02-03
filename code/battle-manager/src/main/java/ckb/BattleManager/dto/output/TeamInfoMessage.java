package ckb.BattleManager.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamInfoMessage {
    List<String> participantsName;
    private Long teamId;
}
