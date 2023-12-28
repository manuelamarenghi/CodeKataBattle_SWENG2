package ckb.AccountManager.repository;

import ckb.AccountManager.model.Role;
import ckb.AccountManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findUserById(Long id);
    Optional<User> findUserByEmailAndPassword(String email, String password);
    Optional<List<User>> findUserByRole(Role role);
    Optional<User> findUserByEmail(String email);
}
