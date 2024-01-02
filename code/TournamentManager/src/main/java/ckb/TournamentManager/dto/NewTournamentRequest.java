package ckb.TournamentManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor

public class NewTournamentRequest {
    private Date regdeadline;
}
