package com.example.banksystem.service;

import com.example.banksystem.dto.LoginDto;
import com.example.banksystem.dto.RegisterDto;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.UserRepository;
import com.example.banksystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public void register(RegisterDto dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new RuntimeException("Email already in use");

        User user = new User();
        user.setFullname(dto.getFullname());
        user.setFincode(dto.getFincode());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(false);
        String code = UUID.randomUUID().toString();
        user.setVerificationCode(code);

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), code);
    }

    public void verifyEmail(String code) {
        User user = userRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid verification code"));

        user.setEnabled(true);
        user.setVerificationCode(null);
        userRepository.save(user);
    }

    public String login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.getEnabled())
            throw new RuntimeException("Email not verified");

        if(!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Incorrect password");

        return jwtUtil.generateToken(user.getEmail());
    }
}
