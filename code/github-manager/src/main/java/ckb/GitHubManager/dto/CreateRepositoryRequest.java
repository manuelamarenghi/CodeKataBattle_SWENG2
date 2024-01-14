package ckb.GitHubManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRepositoryRequest {
    private String repoName;
    private List<ImmutablePair<String, String>> files;
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
