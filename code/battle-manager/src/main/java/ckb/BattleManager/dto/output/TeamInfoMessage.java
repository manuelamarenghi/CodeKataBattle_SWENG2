package ckb.BattleManager.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TeamInfoMessage {
    List<String> participantsName;
    private Long teamId;
}
