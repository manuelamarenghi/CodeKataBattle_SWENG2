package ckb.TournamentManager.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor

public class NewTournamentRequest {
    private Long creatorID;
    private String name;
    private Date regdeadline;
}
