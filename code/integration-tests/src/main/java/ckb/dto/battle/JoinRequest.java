package ckb.dto.battle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequest {
    private Long idStudent;
    private Long idBattle;
}
