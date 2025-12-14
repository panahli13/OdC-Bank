package com.example.banksystem.controller;

import com.example.banksystem.dto.LoginDto;
import com.example.banksystem.dto.RegisterDto;
import com.example.banksystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto dto) {
        authService.register(dto);
        return ResponseEntity.ok("User registered successfully. Check your email to verify.");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String code) {
        authService.verifyEmail(code);
        return ResponseEntity.ok("Email verified successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(token);
    }
}
