package ckb.AccountManager.controller;

import ckb.AccountManager.dto.SignUpRequest;
import ckb.AccountManager.model.Role;
import ckb.AccountManager.model.User;
import ckb.AccountManager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SignUpControllerTest {
    @Autowired
    private SignUpController signUpController;
    @Autowired
    private UserRepository userRepository;

    /**
     * not using @BeforeEach because using @Autowired on static fields is not allowed
     **/

    @Test
    public void correctTest() {
        deleteTestUser();
        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());

        User user = userRepository.findUserByEmail("ckb.test.user@mail.ckb").orElse(null);

        deleteTestUser();
        assertNotNull(user);
    }

    @Test
    public void emailInUseTest() {
        createTestUser();
        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        deleteTestUser();

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingEmailTest() {
        SignUpRequest request = new SignUpRequest("", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingNameTest() {
        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "", "password", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingPasswordTest() {
        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "Test User", "", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void invalidRoleTest() {
        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "Test User", "password", null);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    private void createTestUser() {
        deleteTestUser();

        User user = new User();
        user.setFullName("Test User");
        user.setEmail("ckb.test.user@mail.ckb");
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        userRepository.save(user);
    }

    private void deleteTestUser() {
        userRepository
                .findUserByEmail("ckb.test.user@mail.ckb")
                .ifPresent(user -> userRepository.delete(user));
    }
}
