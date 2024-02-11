package ckb.TournamentManager.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPermissionRequest {
    private Long tournamentId;
    private Long educatorId;
}
