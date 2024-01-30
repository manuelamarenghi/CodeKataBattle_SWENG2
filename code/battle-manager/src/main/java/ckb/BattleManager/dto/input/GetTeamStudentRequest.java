package ckb.BattleManager.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetTeamStudentRequest {
    private Long battleId;
    private Long studentId;
}
