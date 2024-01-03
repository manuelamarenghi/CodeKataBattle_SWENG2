package ckb.MailService.controller;

import ckb.MailService.dto.AllStudentsMailRequest;
import ckb.MailService.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail/all-students")
@Slf4j
@CrossOrigin(origins = "*")
public class AllStudentsEmailSender extends EmailSender {


    private final MailService mailService;
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> sendEmail(@RequestBody AllStudentsMailRequest request) {

        List<String> addresses;
        try {
            addresses = getEmailAddresses();
        } catch (Exception e) {
            log.error("Error while retrieving email address for students\n");
            return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (mailService.sendEmail(addresses, request.getContent())) {
            log.info("Email sent to {}\n", addresses);
            return new ResponseEntity<>(getHeaders(), HttpStatus.OK);
        }

        log.error("No valid address found students\n");
        return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
    }

    private List<String> getEmailAddresses() {
        return webClient.get()
                .uri(accountManagerUrl + "/api/account/mail-students")
                .retrieve()
                .bodyToMono(String.class)
                .flatMapMany(responseBody -> Flux.fromArray(responseBody.split(",")))
                .map(String::trim)
                .collectList()
                .block();
    }
}
