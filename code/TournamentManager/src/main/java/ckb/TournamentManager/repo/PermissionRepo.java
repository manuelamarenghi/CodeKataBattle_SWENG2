package ckb.TournamentManager.repo;

import ckb.TournamentManager.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface PermissionRepo extends JpaRepository<Permission, Long> {
   /* Optional<Permission> findBytournament_id(@Param("tournament_id")Long tournament_id);
    Optional<Permission> findByuser_id(@Param("user_id")Long user_id);*/
    @Override
    <S extends Permission> S save(S entity);
}
