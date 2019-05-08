package com.eimt.usermanagement.service.impl;

import com.eimt.usermanagement.model.Role;
import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.model.VerificationToken;
import com.eimt.usermanagement.model.dto.UserDto;
import com.eimt.usermanagement.model.exception.DuplicateUserException;
import com.eimt.usermanagement.model.exception.InvalidTokenException;
import com.eimt.usermanagement.model.exception.UserNotFoundException;
import com.eimt.usermanagement.repository.UserRepository;
import com.eimt.usermanagement.repository.VerificationTokenRepository;
import com.eimt.usermanagement.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Calendar;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserDto user) {

        userRepository.findByEmail(user.getEmail()).ifPresent(it -> { throw new DuplicateUserException(
                "User with an email: " + user.getEmail() + " already exists !"
        ); });

        final LocalDateTime registrationDate = LocalDateTime.now();
        final Role role = Role.USER;

        User newUser = new User(
                user.getEmail(),
                passwordEncoder.encode(user.getPassword()),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getBirthDate(),
                registrationDate,
                role,
                null
        );
        return this.userRepository.save(newUser);
    }

    public VerificationToken createVerificationToken(User newUser) {
        VerificationToken verificationToken = new VerificationToken(newUser);
        return this.verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public void verifyUser(String activationCode) {
        VerificationToken token = this.verificationTokenRepository.findByToken(activationCode)
                .orElseThrow(() -> new InvalidTokenException("this code " + activationCode + " is invalid !"));
        User u = this.userRepository.findByEmail(token.getUser().getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with email " + token.getUser().getEmail() + " does not exist !"));
        u.activate();
        this.verificationTokenRepository.delete(token);
    }

    @Scheduled(cron = "0 17 * * * *")
    public void deleteExpiredTokensAndUsers(){
        Calendar cal = Calendar.getInstance();
        verificationTokenRepository.findAll()
                .stream()
                .filter(token -> (token.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0)
                .forEach(t -> {
                    User user = t.getUser();
                    verificationTokenRepository.delete(t);
                    userRepository.delete(user);
                });
    }

    public void emailActivationHasExpired(String email) {

    }

    public void changePassword(String email, String password) {

    }

    public void activateUser(String token) {

    }

    public void getUserDetails(String email) {

    }

    public void changeFirstName(String email, String newFirstName) {

    }

    public void changeLastName(String email, String newLastName) {

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
