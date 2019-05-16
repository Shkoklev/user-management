package com.eimt.usermanagement.service;

import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.model.VerificationToken;
import com.eimt.usermanagement.model.dto.UserDto;
import com.eimt.usermanagement.model.dto.UserEditObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;


public interface UserService extends UserDetailsService {

    User createUser(UserDto employee);

    VerificationToken createVerificationToken(User newUser);

    String generateNewPasswordForEmail(String email);

    User editUser(User employee, UserEditObject employeeEditObject);

    Page<User> getUsers(Pageable pageable);

    User getUser(String email);

    User createUser(User user);

    void deleteUserByEmail(String email);

    void verifyAndActivateUser(String token);

    boolean isPasswordValid(User e, String password);

    void updatePassword(User e, String newPassword);

}
