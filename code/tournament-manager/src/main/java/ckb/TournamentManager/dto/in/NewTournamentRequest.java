package ckb.TournamentManager.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class NewTournamentRequest {
    private Long creatorID;
    private String name;
    private Date regdeadline;
}
