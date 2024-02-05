package ckb.BattleManager.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DirectMailRequest {
    private List<String> userIDs;
    private String content;
}
