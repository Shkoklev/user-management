package com.eimt.usermanagement.service;

import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.model.VerificationToken;
import com.eimt.usermanagement.model.dto.UserDto;

public interface UserService {

    User createUser(UserDto employee);

    VerificationToken createVerificationToken(User newUser);

    void emailActivationHasExpired(String email);

    void changePassword(String email, String password);

    void verifyUser(String token);

    void getUserDetails(String email);

    void changeFirstName(String email, String newFirstName);

    void changeLastName(String email, String newLastName);

}
