package ckb.TournamentManager.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PermissionRequest {
    private Long tournamentID;
    private Long userID;
    private Long creatorID;
}
