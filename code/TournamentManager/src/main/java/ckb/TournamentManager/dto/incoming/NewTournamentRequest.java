package ckb.TournamentManager.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor

public class NewTournamentRequest {
    private Date regdeadline;

    @Data
    @Builder
    @AllArgsConstructor

    public static class GetTournamentPageRequest {
        private Long tournamentID;
    }
}
