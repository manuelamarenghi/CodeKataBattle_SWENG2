package ckb.BattleManager.dto.output;

import ckb.BattleManager.model.WorkingPair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRepositoryRequest {
    private String repoName;
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
