package ckb.dto.battle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBattleRequest {
    private Long tournamentId;
    private Long authorId;
    private Integer minStudents;
    private Integer maxStudents;
    private Boolean battleToEval;
    private LocalDateTime regDeadline;
    private LocalDateTime subDeadline;
}
