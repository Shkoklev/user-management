package com.eimt.usermanagement.repository;

import com.eimt.usermanagement.model.SsoIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SsoIdentityRepository extends JpaRepository<SsoIdentity, String> {
}
