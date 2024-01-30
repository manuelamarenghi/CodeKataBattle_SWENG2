package ckb.dto.battle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptStudentTeamRequest {
    private Long idStudent;
    private Long idTeam;
}
