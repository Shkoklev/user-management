package com.eimt.usermanagement.service;

import com.eimt.usermanagement.model.SsoIdentity;
import com.eimt.usermanagement.model.User;

import java.util.Optional;

public interface SsoIdentityService {

    Optional<User> findUser(String ssoId);

    SsoIdentity createSsoIdentity(String ssoId, User e);
}
