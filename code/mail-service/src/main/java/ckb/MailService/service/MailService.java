package ckb.MailService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    public boolean sendEmail(List<String> addresses, String content) {
        SimpleMailMessage message = new SimpleMailMessage();

        if (addresses.isEmpty()) { // no email addresses to send to -- error
            return false;
        }
        if (addresses.size() > 1) {
            message.setBcc(addresses.subList(1, addresses.size()).toArray(new String[0]));
        }

        message.setTo(addresses.getFirst()); // must set the To field
        message.setSubject("CKB - Notification");
        message.setText(content);

        try {
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Error while sending email to {}\n", addresses);
            return false;
        }
    }
}
