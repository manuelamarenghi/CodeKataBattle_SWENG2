package ckb.BattleManager.repository;

import ckb.BattleManager.model.Battle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BattleRepository extends JpaRepository<Battle, Long> {
    // I can extend with my methods
    // @Query("SELECT s FROM Student s WHERE s.email = ?1")
    // Optional<Student> findStudentByEmail(String email)
    // SELECT * student WHERE email = ...
}
