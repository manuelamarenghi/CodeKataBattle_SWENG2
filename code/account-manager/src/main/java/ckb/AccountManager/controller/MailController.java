package ckb.AccountManager.controller;

import ckb.AccountManager.dto.MailRequest;
import ckb.AccountManager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account/mail")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MailController extends Controller {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> mail(@RequestBody MailRequest request) {
        List<String> userIDs = List.of(request.getUserIDs().split(","));

        String mailAddresses = userIDs.stream()
                .map(userID -> {
                    try {
                        return userService.getUserById(Long.valueOf(userID.trim())).getEmail();
                    } catch (Exception e) {
                        log.error("Mail for userID: {} not found", userID);
                        return "";
                    }
                })
                .collect(Collectors.joining(", "));

        if (mailAddresses.isEmpty()){
            log.error("No mail found for userIDs: {} not found", userIDs);
            return new ResponseEntity<>(null, getHeaders(), HttpStatus.NOT_FOUND);
        }

        log.info("Mail addresses: {}", mailAddresses);
        return new ResponseEntity<>(mailAddresses, getHeaders(), HttpStatus.OK);
    }
}
