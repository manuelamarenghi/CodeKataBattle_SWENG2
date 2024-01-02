package ckb.TournamentManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class PermissionRequest {
    private Long tournamentID;
    private Long userID;
}
