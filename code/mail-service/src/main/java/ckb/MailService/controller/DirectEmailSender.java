package ckb.MailService.controller;

import ckb.AccountManager.dto.MailRequest;
import ckb.MailService.dto.DirectMailRequest;
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
@RequestMapping("/api/mail/direct")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DirectEmailSender extends EmailSender {

    private final WebClient webClient;
    private final MailService mailService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> sendEmail(@RequestBody DirectMailRequest request) {

        List<String> userIDs = request.getUserIDs();
        List<String> addresses;
        try {
            addresses = getEmailAddresses(userIDs);
        } catch (Exception e) {
            log.error("Error while retrieving email address for users {}\n", userIDs);
            return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (mailService.sendEmail(addresses, request.getContent())) {
            log.info("Email sent to {}\n", addresses);
            return new ResponseEntity<>(getHeaders(), HttpStatus.OK);
        }

        log.error("No valid address found for: {}\n", userIDs);
        return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
    }

    private List<String> getEmailAddresses(List<String> userIDs) {
        return webClient.post()
                .uri(accountManagerUrl + "/api/account/mail")
                .bodyValue(new MailRequest(userIDs))
                .retrieve()
                .bodyToMono(String.class) // we expect the response to only be a String containing the email addresses
                // clean up the response...
                .map(a -> a.replace(" ", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace("\"", ""))
                .flatMapMany(responseBody -> Flux.fromArray(responseBody.split(",")))
                .collectList() // collect the response into a list
                .block(); // block until the response is received
    }
}
