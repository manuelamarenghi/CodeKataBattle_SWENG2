package ckb.AccountManager.service;

import ckb.AccountManager.dto.SignUpRequest;
import ckb.AccountManager.dto.UpdateRequest;
import ckb.AccountManager.model.Role;
import ckb.AccountManager.model.User;
import ckb.AccountManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(SignUpRequest request) {
        User user = new User();

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        userRepository.save(user);
    }

    public boolean emailInUse(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean accountExists(String email, String password) {
        return userRepository.findUserByEmailAndPassword(email, password).isPresent();
    }

    public User getUserById(Long id) {
        return userRepository.findUserById(id).orElse(null);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findUserByRole(role).orElse(null);
    }

    public Long getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElse(null);
        return user == null ? null : user.getId();
    }

    public boolean updateUser(UpdateRequest request) {
        User user = userRepository.findUserById(request.getId()).orElse(null);

        if (user == null) return false;
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        userRepository.save(user);
        return true;
    }
}
