package com.eimt.usermanagement.model;

import javax.persistence.*;

@Entity
@Table(name = "sso_identities")
public class SsoIdentity {

    @Id
    String ssoId;
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    // JPA only
    public SsoIdentity() {
    }

    public SsoIdentity(String ssoId, User user) {
        this.ssoId = ssoId;
        this.user = user;
    }

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
