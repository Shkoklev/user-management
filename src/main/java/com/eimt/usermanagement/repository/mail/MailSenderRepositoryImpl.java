package com.eimt.usermanagement.repository.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;


@Repository
public class MailSenderRepositoryImpl implements MailSenderRepository {

    JavaMailSender mailSender;

    public MailSenderRepositoryImpl(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    @Override
    public void sendMail(String to, String subject, String confirmationUrl, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message + " \n" + confirmationUrl);
        mailSender.send(email);
    }
}
