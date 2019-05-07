package com.eimt.usermanagement.repository.mail;

import java.util.Map;

public interface MailSenderRepository {

    void sendMail(String to, String subject, String confirmationUrl, String message);

}
