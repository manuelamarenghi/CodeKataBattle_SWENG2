package ckb.TournamentManager.dto.incoming;

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
