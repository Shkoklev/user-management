package com.eimt.usermanagement.service.impl;

import com.eimt.usermanagement.model.Role;
import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.model.VerificationToken;
import com.eimt.usermanagement.model.dto.UserDto;
import com.eimt.usermanagement.model.dto.UserEditObject;
import com.eimt.usermanagement.model.exception.DuplicateUserException;
import com.eimt.usermanagement.model.exception.InvalidTokenException;
import com.eimt.usermanagement.model.exception.UserNotFoundException;
import com.eimt.usermanagement.repository.UserRepository;
import com.eimt.usermanagement.repository.VerificationTokenRepository;
import com.eimt.usermanagement.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserDto user) {

        userRepository.findByEmail(user.getEmail()).ifPresent(it -> {
            throw new DuplicateUserException(
                    "User with an email: " + user.getEmail() + " already exists !"
            );
        });

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
    public void verifyAndActivateUser(String activationCode) {
        VerificationToken token = this.verificationTokenRepository.findByToken(activationCode)
                .orElseThrow(() -> new InvalidTokenException("this code " + activationCode + " is invalid !"));
        User u = this.userRepository.findByEmail(token.getUser().getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with email " + token.getUser().getEmail() + " does not exist !"));
        u.activate();
        this.verificationTokenRepository.delete(token);
    }

    @Scheduled(cron = "0 17 * * * *")
    public void deleteExpiredTokensAndUsers() {
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

    public String generateNewPasswordForEmail(String email) {
        User u = (User) this.getUser(email);
        String password = RandomStringUtils.randomAlphanumeric(8);
        u.changePassword(passwordEncoder.encode(password));
        userRepository.save(u);
        return password;
    }

    @Transactional
    public User editUser(User user, UserEditObject userEditObject) {
        user.changeFirstName(userEditObject.getFirstName());
        user.changeLastName(userEditObject.getLastName());
        user.changeBirthDate(userEditObject.getBirthDate());
        user.changeGender(userEditObject.getGender());
        this.userRepository.save(user);
        return user;
    }

    public boolean isPasswordValid(User e, String password) {
        return this.passwordEncoder.matches(password, e.getPassword());
    }

    @Override
    @Transactional
    public void updatePassword(User u, String newPassword) {
        u.changePassword(this.passwordEncoder.encode(newPassword));
        userRepository.save(u);
    }

    @Override
    public User createUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    @Override
    public User getUser(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    @Transactional
    public void deleteUserByEmail(String email) {
        this.userRepository.deleteByEmail(email);
    }

    @Override
    public void activateUser(User user) {
        user.activate();
        this.userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
