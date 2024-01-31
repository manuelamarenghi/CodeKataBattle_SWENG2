package ckb.TournamentManager.dto.outcoming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListBattlesResponse {
    List<Long> battlesID;
}
