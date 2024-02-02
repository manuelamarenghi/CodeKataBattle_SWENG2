package ckb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament {
    private Long tournamentID;
    private String name;
    private Date regdeadline;
    private Boolean status;
    private Long creatorID;
}
