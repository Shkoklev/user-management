package com.eimt.usermanagement.model.dto;

import java.time.LocalDate;

public class UserDto {

    public String email;

    private String password;

    private String verifiedPassword;

    private String firstName;

    private String lastName;

    private String gender;

    private LocalDate birthDate;

    public boolean verifyPassword() {
        return password.equals(verifiedPassword);
    }
}
