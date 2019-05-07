package com.eimt.usermanagement.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String gender;

    private LocalDate birthDate;

    private LocalDateTime registrationDate;

    private Role role;

    private boolean enabled;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public User(){}

    public User(String email,
                String password,
                String firstName,
                String lastName,
                String gender,
                LocalDate birthDate,
                LocalDateTime registrationDate,
                Role role,
                Department department) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.registrationDate = registrationDate;
        this.role = role;
        this.department = department;
        this.enabled = false;
    }

    public String getEmail() {
        return email;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void changeFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void changeLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void changeGender(String gender) {
        this.gender = gender;
    }

    public LocalDate changeBirthDate() {
        return birthDate;
    }

    public void changeBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void changeRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Role getRole() {
        return role;
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void changeDepartment(Department department) {
        this.department = department;
    }

    public boolean isActivated() {
        return enabled;
    }

    public void activate() {
        this.enabled = true;
    }

}
