package ckb.TournamentManager.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformStudentsRequest {
    private Long tournamentId;
    private String battleName;
}
