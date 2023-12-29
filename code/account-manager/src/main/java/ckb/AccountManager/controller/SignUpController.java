package ckb.AccountManager.controller;

import ckb.AccountManager.dto.SignUpRequest;
import ckb.AccountManager.model.Role;
import ckb.AccountManager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/account/sign-up")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SignUpController extends Controller {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> signUp(@RequestBody SignUpRequest request) {
        // check if the request has valid data

        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;

        userService.createUser(request);
        log.info("Account created for email {}", request.getEmail());

        return new ResponseEntity<>("Account created", getHeaders(), HttpStatus.CREATED);
    }


    private ResponseEntity<Object> checkRequest(SignUpRequest request) {
        if (emailNotValid(request.getEmail())) return new ResponseEntity<>("Invalid email", getHeaders(), HttpStatus.BAD_REQUEST);
        if (missingName(request.getFullName())) return new ResponseEntity<>("Full name is required", getHeaders(), HttpStatus.BAD_REQUEST);
        if (missingPassword(request.getPassword())) return new ResponseEntity<>("Password is required", getHeaders(), HttpStatus.BAD_REQUEST);
        if (invalidRole(request.getRole())) return new ResponseEntity<>("Role is required", getHeaders(), HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean emailNotValid(String email) {
        if (email == null || email.isEmpty()) {
            log.error("Email not provided for update on user {}", email);
            return true;
        }
        if (userService.emailInUse(email)) {
            log.error("Email {} already in use", email);
            return true;
        }

        // check if the email is valid
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
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

}
