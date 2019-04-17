package com.eimt.usermanagement.service;

import com.eimt.usermanagement.model.dto.UserDto;

public interface UserService {

    void register(UserDto employee);

    void emailActivationHasExpired(String email);

    void changePassword(String email, String password);

    void activateUser(String token);

    void getUserDetails(String email);

    void changeFirstName(String email, String newFirstName);

    void changeLastName(String email, String newLastName);

}
