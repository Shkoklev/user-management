package com.eimt.usermanagement.repository;

import com.eimt.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    void sendEmail(String email,String token);

    void deleteByEmail(String email);

    Optional<User> findByEmail(String email);

}
