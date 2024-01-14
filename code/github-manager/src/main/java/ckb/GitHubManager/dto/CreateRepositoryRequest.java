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

    [
        {
            "path/to/file": "some example text content"
        },
        {
            "path/to/file2": "some other file content"
        }
    ]
    */
}
