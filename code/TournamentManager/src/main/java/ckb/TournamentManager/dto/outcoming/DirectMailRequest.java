package ckb.TournamentManager.dto.outcoming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DirectMailRequest {
    private List<String> userIDs;
    private String content;
}
