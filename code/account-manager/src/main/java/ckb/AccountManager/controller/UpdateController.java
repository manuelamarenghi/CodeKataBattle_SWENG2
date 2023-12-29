package ckb.AccountManager.controller;

import ckb.AccountManager.dto.UpdateRequest;
import ckb.AccountManager.model.Role;
import ckb.AccountManager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account/update")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UpdateController extends Controller {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updatePersonalInformation(@RequestBody UpdateRequest request) {
        // check if the request has valid data
        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;

        // execute the request
        try {
            if (!userService.updateUser(request)) throw new Exception();
        } catch (Exception e) {
            log.error("Error updating user {}", request.getId(), e);
            return new ResponseEntity<>("Error updating user", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("Data for user: {} updated successfully", request.getId());
        return new ResponseEntity<>("Data Updated", getHeaders(), HttpStatus.OK);
    }

    private ResponseEntity<Object> checkRequest(UpdateRequest request) {
        if (userIDNotValid(request.getId())) return new ResponseEntity<>("Invalid user ID", getHeaders(), HttpStatus.BAD_REQUEST);
        if (emailNotValid(request.getId(), request.getEmail())) return new ResponseEntity<>("Invalid email", getHeaders(), HttpStatus.BAD_REQUEST);
        if (missingName(request.getFullName())) return new ResponseEntity<>("Full name is required", getHeaders(), HttpStatus.BAD_REQUEST);
        if (missingPassword(request.getPassword())) return new ResponseEntity<>("Password is required", getHeaders(), HttpStatus.BAD_REQUEST);
        if (invalidRole(request.getRole())) return new ResponseEntity<>("Role is required", getHeaders(), HttpStatus.BAD_REQUEST);
        if (userNotFound(request.getId())) return new ResponseEntity<>("User not found", getHeaders(), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean userIDNotValid(Long id) {
        return userService.getUserById(id) == null;
    }

    private boolean emailNotValid(Long id, String email) {
        if (email == null || email.isEmpty()) {
            log.error("Email not provided for update on user {}", email);
            return true;
        }
        if (emailDiffersFromCurrent(id ,email) && userService.emailInUse(email)) {
            log.error("Email {} already in use", email);
            return true;
        }
        return false;
    }

    private boolean emailDiffersFromCurrent(Long id, String newEmail) {
        String currentEmail = userService.getUserById(id).getEmail();
        return !currentEmail.equals(newEmail);
    }

    private boolean missingName(String name) {
        if (name == null || name.isEmpty()) {
            log.error("Name not provided for update on user {}", name);
            return true;
        }
        return false;
    }

    private boolean missingPassword(String password) {
        if (password == null || password.isEmpty()) {
            log.error("Password not provided for update on user {}", password);
            return true;
        }
        return false;
    }

    private boolean invalidRole(Role role) {
        if (role != Role.STUDENT && role != Role.EDUCATOR) {
            log.error("Role not valid for update on user {}", role);
            return true;
        }
        return false;
    }

    private boolean userNotFound(Long id) {
        if (userService.getUserById(id) == null) {
            log.error("User {} not found", id);
            return true;
        }
        return false;
    }
}
