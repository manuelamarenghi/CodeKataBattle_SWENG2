package ckb.TournamentManager.repo;

import ckb.TournamentManager.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface PermissionRepo extends JpaRepository<Permission, Long> {
    Optional<Permission> findByTournamentIDAndUserID(@Param("tournamentID") Long tournamentID, @Param("userID") Long userID);
    List<Permission> findAllByTournamentID(@Param("tournamentID") Long tournamentID);
    @Override
    <S extends Permission> S save(S entity);
    void delete(Permission entity);

    @Query(value = "DELETE FROM permissions p1_0 WHERE p1_0.tournamentid = ?1", nativeQuery = true)
    void deleteAllByTournamentID(@Param("tournamentID") Long tournamentID);
    void deleteByTournamentID(@Param("tournamentID") Long tournamentID);
}
