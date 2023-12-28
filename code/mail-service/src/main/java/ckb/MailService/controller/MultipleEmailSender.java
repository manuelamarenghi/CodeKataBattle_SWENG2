package ckb.MailService.controller;

import ckb.MailService.dto.MultipleMailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RestController
@RequestMapping("/api/mail/multiple")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MultipleEmailSender extends EmailSender {

    private final JavaMailSender mailSender;
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> sendEmail(@RequestBody MultipleMailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();

        List<String> userIDs = request.getUserIDs();
        List<String> addresses;
        try {
            addresses = getEmailAddresses(userIDs);
        } catch (Exception e) {
            log.error("Error while retrieving email address for users {}\n", userIDs);
            return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (addresses.size() > 1) {
            // need to identify a mail for the "to" field and an array of mails for the "bcc" field
            message.setTo(addresses.getFirst());
            message.setBcc(addresses.subList(1, addresses.size()).toArray(new String[0]));
        } else if (addresses.size() == 1) { // only one valid address was retrieved
            log.warn("Only one valid address found in: {}\n", request.getUserIDs());
            log.warn("Consider sending requests to /api/mail/single\n");
            message.setTo(addresses.getFirst());
        } else { // no valid address was retrieved
            log.error("No email was sent due to no valid addresses found in: {}\n", request.getUserIDs());
            log.error("maybe no valid userID was provided?\n");
            return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
        }

        message.setSubject("CKB - Notification");
        message.setText(request.getContent());

        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error while sending email to {}\n", addresses);
            return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
        }
        log.info("Email sent to {}\n", addresses);
        return new ResponseEntity<>(getHeaders(), HttpStatus.OK);
    }

    private List<String> getEmailAddresses(List<String> userIDs) {
        // request will be constructed like this: http://localhost:8080/api/mail/single?userID=1&userID=2&userID=3 ...
        return webClient.get()
                .uri("http://localhost:8080/api/account/mail",
                        uriBuilder -> uriBuilder.queryParam("userID", userIDs).build())
                .retrieve()
                .bodyToMono(String.class) // we expect the response to only be a String containing the email addresses
                .flatMapMany(responseBody -> Flux.fromArray(responseBody.split(",")))
                .map(String::trim)
                .collectList() // collect the response into a list
                .block(); // block until the response is received
    }
}
