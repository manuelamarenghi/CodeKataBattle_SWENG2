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
        // check if the request is valid
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            log.error("Email not provided for update on user {}", request.getId());
            return new ResponseEntity<>("Email is required", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (request.getFullName() == null || request.getFullName().isEmpty()) {
            log.error("Name not provided for update on user {}", request.getId());
            return new ResponseEntity<>("Full name is required", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            log.error("Password not provided for update on user {}", request.getId());
            return new ResponseEntity<>("Password is required", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (request.getRole() != Role.STUDENT && request.getRole() != Role.EDUCATOR) {
            log.error("Role not valid for update on user {}", request.getId());
            return new ResponseEntity<>("Role is required", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (userService.getUserById(request.getId()) == null) {
            log.error("User {} not found", request.getId());
            return new ResponseEntity<>("User not found", getHeaders(), HttpStatus.NOT_FOUND);
        }

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
}
