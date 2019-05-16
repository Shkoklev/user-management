package com.eimt.usermanagement.model;

import net.bytebuddy.utility.RandomString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class VerificationToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "employee_id")
    private User user;

    private Date expiryDate;

    public VerificationToken() {
    }

    public VerificationToken(User user) {
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
        this.token = UUID.randomUUID().toString();
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public static int getEXPIRATION() {
        return EXPIRATION;
    }

    public Long getId() {
        return id;
    }

    public void changeId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void changeToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void changeUser(User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void changeExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
