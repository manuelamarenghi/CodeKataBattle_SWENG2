package ckb.TournamentManager.model;

import jakarta.persistence.Column;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class PermissionId implements Serializable {
    @Column(name = "tournamentID")
    private Long tournamentID;
    @Column(name = "userID")
    private Long userID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionId that = (PermissionId) o;
        return Objects.equals(tournamentID, that.tournamentID) &&
                Objects.equals(userID, that.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tournamentID, userID);
    }
}
