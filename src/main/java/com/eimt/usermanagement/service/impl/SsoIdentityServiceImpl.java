package com.eimt.usermanagement.service.impl;

import com.eimt.usermanagement.model.SsoIdentity;
import com.eimt.usermanagement.model.User;
import com.eimt.usermanagement.repository.SsoIdentityRepository;
import com.eimt.usermanagement.service.SsoIdentityService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SsoIdentityServiceImpl implements SsoIdentityService {

    private final SsoIdentityRepository ssoIdentityRepository;

    public SsoIdentityServiceImpl(SsoIdentityRepository ssoIdentityRepository) {
        this.ssoIdentityRepository = ssoIdentityRepository;
    }

    @Override
    public Optional<User> findUser(String ssoId) {
        return ssoIdentityRepository.findById(ssoId)
                .map(it -> Optional.of(it.getUser()))
                .orElse(Optional.empty());
    }

    @Override
    public SsoIdentity createSsoIdentity(String ssoId, User e) {

        return this.ssoIdentityRepository.save(new SsoIdentity(ssoId, e));
    }
}
