package ckb.SolutionEvaluationService.dto.in;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationParamsResponse {
    private String repoLink;
    private boolean security;
    private boolean reliability;
    private boolean maintainability;
}
