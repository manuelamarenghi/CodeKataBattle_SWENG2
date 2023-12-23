package ckb.MailService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SingleMailRequest {
    private String userID;
    private String subject;
    private String content;

}
