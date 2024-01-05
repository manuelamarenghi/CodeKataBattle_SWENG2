package ckb.AccountManager.controller;


import ckb.AccountManager.dto.in.UserRequest;
import ckb.AccountManager.model.User;
import ckb.AccountManager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account/user")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController extends Controller {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUser(@RequestBody UserRequest request) {
        User user = userService.getUserById(request.getUserID());
        if (user == null) {
            log.error("User not found for id {}", request.getUserID());
            return new ResponseEntity<>("User not found", getHeaders(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, getHeaders(), HttpStatus.OK);
    }
}
