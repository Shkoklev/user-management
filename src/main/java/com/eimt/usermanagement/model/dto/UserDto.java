package com.eimt.usermanagement.model.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class UserDto {

    @NotNull
    @NotEmpty
    @Email
    public String email;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String verifiedPassword;

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    private String gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    public UserDto() {
    }

    public UserDto(String email, String password, String verifiedPassword, String firstName,
                   String lastName, String gender, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.verifiedPassword = verifiedPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifiedPassword() {
        return verifiedPassword;
    }

    public void setVerifiedPassword(String verifiedPassword) {
        this.verifiedPassword = verifiedPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
