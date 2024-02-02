package ckb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Participation {
    private Long id;
    private Long studentId;
    private Team team;

    @Override
    public String toString() {
        return studentId.toString();
    }
}