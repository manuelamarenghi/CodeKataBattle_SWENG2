package ckb.AccountManager.controller;

import ckb.AccountManager.model.Role;
import ckb.AccountManager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account/mail-students")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MailStudentsController extends Controller{
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> mailStudents() {
        String mailAddresses = userService
                .getUsersByRole(Role.STUDENT)
                .stream()
                .collect(StringBuilder::new, (sb, user) -> sb.append(user.getEmail()).append(", "), StringBuilder::append)
                .toString();

        if (mailAddresses.isEmpty()) {
            log.error("No students found");
            return new ResponseEntity<>("No students found", getHeaders(), HttpStatus.NOT_FOUND);
        }

        mailAddresses = mailAddresses.substring(0, mailAddresses.length() - 2);
        log.info("Students found: {}", mailAddresses);
        return new ResponseEntity<>(mailAddresses, getHeaders(), HttpStatus.OK);
    }
}
