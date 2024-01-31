package ckb.dto.battle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignPersonalScoreRequest {
    private Long idTeam;
    private Integer score;
    private Long idEducator;
}
