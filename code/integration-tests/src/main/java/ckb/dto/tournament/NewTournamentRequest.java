package ckb.dto.tournament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor

public class NewTournamentRequest {
    private Date regdeadline;
    private Long creatorID;
}
