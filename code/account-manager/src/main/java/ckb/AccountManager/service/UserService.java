package ckb.AccountManager.service;

import ckb.AccountManager.dto.SignUpRequest;
import ckb.AccountManager.model.User;
import ckb.AccountManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


}
