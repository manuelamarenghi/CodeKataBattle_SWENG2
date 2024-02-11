package ckb.BattleManager.dto.in;

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
