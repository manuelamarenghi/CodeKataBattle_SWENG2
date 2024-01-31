package ckb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Battle {
    private Long battleId;
    private String name;
    private Long tournamentId;
    private String repositoryLink;
    private List<Team> teamsRegistered;
    private int minStudents;
    private int maxStudents;
    private LocalDateTime regDeadline;
    private LocalDateTime subDeadline;
    private Boolean battleToEval;
    private Long authorId;
    private Boolean hasStarted;
    private Boolean hasEnded;
    private Boolean isClosed;
}
