package com.eimt.usermanagement.ports.listeners;

import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.model.VerificationToken;
import com.eimt.usermanagement.model.event.OnRegistrationCompleteEvent;
import com.eimt.usermanagement.repository.mail.MailSenderRepository;
import com.eimt.usermanagement.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {


    MailSenderRepository mailSenderRepository;

    UserService userService;

    public RegistrationListener(UserService userService, MailSenderRepository mailSenderRepository) {
        this.userService = userService;
        this.mailSenderRepository = mailSenderRepository;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        VerificationToken token = userService.createVerificationToken(user);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl =
                 event.getAppUrl() + "/registrationConfirm?token=" + token.getToken();
        String message = ("You registered successfully. We will send you a confirmation message to your email account.");

        mailSenderRepository.sendMail(recipientAddress,subject,confirmationUrl,message);
    }
}
