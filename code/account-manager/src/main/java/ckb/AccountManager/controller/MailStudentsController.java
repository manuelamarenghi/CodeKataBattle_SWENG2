package ckb.AccountManager.controller;

import ckb.AccountManager.model.Role;
import ckb.AccountManager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/account/mail-students")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MailStudentsController extends Controller {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getStudentMails() {
        String mailAddresses = userService
                .getUsersByRole(Role.STUDENT)
                .stream()
                .collect(StringBuilder::new, (sb, user) -> sb.append(user.getEmail()).append(", "), StringBuilder::append)
                .toString();

        if (mailAddresses.isEmpty()) {
            log.warn("No students found, maybe the database is empty?");
            return new ResponseEntity<>("", getHeaders(), HttpStatus.OK);
        }

        List<String> addresses = Arrays.stream(mailAddresses.split(","))
                .map(String::trim)
                .toList();

        log.info("Students found: {}", addresses);
        return new ResponseEntity<>(addresses, getHeaders(), HttpStatus.OK);
    }
}
