package ckb.AccountManager.controller;

import ckb.AccountManager.dto.UpdateRequest;
import ckb.AccountManager.model.Role;
import ckb.AccountManager.model.User;
import ckb.AccountManager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UpdateControllerTest {

    @Autowired
    private UpdateController updateController;
    @Autowired
    private UserRepository userRepository;

    /** not using @BeforeEach because using @Autowired on static fields is not allowed **/

    @Test
    public void correctTest() {
        Long id = createTestUser();

        UpdateRequest request = new UpdateRequest(id, "ckb.test.user@mail.ckb", "Test User", "new_password", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());

        User updatedUser = userRepository.findUserById(id).orElse(null);
        assert updatedUser != null; // should never happen
        assertEquals("ckb.test.user@mail.ckb", updatedUser.getEmail());
        assertEquals("Test User", updatedUser.getFullName());
        assertEquals("new_password", updatedUser.getPassword());
        assertEquals(Role.STUDENT, updatedUser.getRole());

        deleteTestUser();
    }

    @Test
    public void emailInUseTest() {
        Long id = createTestUser();

        UpdateRequest request = new UpdateRequest(id, "catta@mail.com", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        deleteTestUser();

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingEmailTest() {
        Long id = createTestUser();

        UpdateRequest request = new UpdateRequest(id, "", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingNameTest() {
        Long id = createTestUser();

        UpdateRequest request = new UpdateRequest(id, "ckb.test.user@mail.ckb", "", "password", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingPasswordTest() {
        Long id = createTestUser();

        UpdateRequest request = new UpdateRequest(id, "ckb.test.user@mail.ckb", "Test User", "", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void invalidRoleTest() {
        Long id = createTestUser();

        UpdateRequest request = new UpdateRequest(id, "ckb.test.user@mail.ckb", "Test User", "password", null);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void invalidIDTest() {
        createTestUser();

        UpdateRequest request = new UpdateRequest(0L, "ckb.test.user@mail.ckb", "Test User", "password", null);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    private Long createTestUser() {
        deleteTestUser();

        User user = new User();
        user.setFullName("Test User");
        user.setEmail("ckb.test.user@mail.ckb");
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        userRepository.save(user);


        user = userRepository.findUserByEmail("ckb.test.user@mail.ckb").orElse(null);
        assert user != null; // should never happen
        return user.getId();
    }

    private void deleteTestUser() {
        userRepository
                .findUserByEmail("ckb.test.user@mail.ckb")
                .ifPresent(user -> userRepository.delete(user));
    }
}
