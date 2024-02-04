package ckb.BattleManager.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationParamsResponse {
    private String repoLink;
    private Boolean security;
    private Boolean reliability;
    private Boolean maintainability;
}
