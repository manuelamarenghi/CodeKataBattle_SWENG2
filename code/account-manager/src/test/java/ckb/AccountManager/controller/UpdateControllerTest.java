package ckb.AccountManager.controller;

import ckb.AccountManager.dto.UpdateRequest;
import ckb.AccountManager.model.Role;
import ckb.AccountManager.model.User;
import ckb.AccountManager.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpdateControllerTest {

    @Autowired
    private UpdateController updateController;
    @Autowired
    private UserRepository userRepository;

    private long id;

    @BeforeAll
    public void setUp() {
        try {
            User user1 = new User();
            user1.setFullName("Catta");
            user1.setEmail("catta@mail.com");
            user1.setPassword("password");
            user1.setRole(Role.EDUCATOR);
            userRepository.save(user1);
        } catch (DataIntegrityViolationException ignored) {
        }
        try {
            User user2 = new User();
            user2.setFullName("Tommy");
            user2.setEmail("tommy@mail.com");
            user2.setPassword("password");
            user2.setRole(Role.EDUCATOR);
            userRepository.save(user2);
        } catch (DataIntegrityViolationException ignored) {
        }
        try {
            User user3 = new User();
            user3.setFullName("Manu");
            user3.setEmail("manu@mail.com");
            user3.setPassword("password");
            user3.setRole(Role.EDUCATOR);
            userRepository.save(user3);
        } catch (DataIntegrityViolationException ignored) {
        }
    }

    @BeforeEach
    public void createTestUser() {
        deleteTestUser();

        User user = new User();
        user.setFullName("Test User");
        user.setEmail("ckb.test.user@mail.ckb");
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        userRepository.save(user);


        user = userRepository.findUserByEmail("ckb.test.user@mail.ckb").orElse(null);
        assert user != null; // should never happen
        this.id = user.getId();
    }

    @AfterEach
    public void deleteTestUser() {
        userRepository
                .findUserByEmail("ckb.test.user@mail.ckb")
                .ifPresent(user -> userRepository.delete(user));
    }

    @Test
    public void correctTest() {
        UpdateRequest request = new UpdateRequest(id, "ckb.test.user@mail.ckb", "Test User", "new_password", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());

        User updatedUser = userRepository.findUserById(id).orElse(null);
        assert updatedUser != null; // should never happen
        assertEquals("ckb.test.user@mail.ckb", updatedUser.getEmail());
        assertEquals("Test User", updatedUser.getFullName());
        assertEquals("new_password", updatedUser.getPassword());
        assertEquals(Role.STUDENT, updatedUser.getRole());
    }

    @Test
    public void emailInUseTest() {
        setUp();

        UpdateRequest request = new UpdateRequest(id, "catta@mail.com", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingEmailTest() {
        UpdateRequest request = new UpdateRequest(id, "", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void invalidEmailFormatTest() {
        UpdateRequest request = new UpdateRequest(id, "SoMe@#Weird_-'1Wrong@Email.idk....", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);


        userRepository.findUserById(id).ifPresent(user -> userRepository.delete(user));
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingNameTest() {
        UpdateRequest request = new UpdateRequest(id, "ckb.test.user@mail.ckb", "", "password", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingPasswordTest() {
        UpdateRequest request = new UpdateRequest(id, "ckb.test.user@mail.ckb", "Test User", "", Role.STUDENT);
        ResponseEntity<Object> response = updateController.updatePersonalInformation(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void invalidRoleTest() {
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

}
