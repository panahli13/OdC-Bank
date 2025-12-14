package com.example.banksystem.repository;

import com.example.banksystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByVerificationCode(String code);
    Optional<User> findByFullname(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByFincode(String fincode);


}
