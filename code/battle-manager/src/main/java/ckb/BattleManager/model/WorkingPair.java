package ckb.BattleManager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkingPair<L, R> implements Serializable {
    private L left;
    private R right;
}
