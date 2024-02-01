package ckb.BattleManager.dto.input;

import ckb.BattleManager.model.WorkingPair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBattleRequest {
    private Long tournamentId;
    private String name;
    private Long authorId;
    private Integer minStudents;
    private Integer maxStudents;
    private Boolean battleToEval;
    private LocalDateTime regDeadline;
    private LocalDateTime subDeadline;
    private List<WorkingPair<String, String>> files;
    /*
    content in the list should be in the following format:
    {
        "repoName": "test",
        "files": [
            {"path": "some/path", "content": "some content"},
            {"path": "some/other/path", "content": "some other content"}
        ]
    }
     */
}
