package ckb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team {
    private Long teamId;
    private Battle battle;
    private List<Participation> participation;
    private String repositoryLink;
    private Integer score;
    private Boolean eduEvaluated;
}
