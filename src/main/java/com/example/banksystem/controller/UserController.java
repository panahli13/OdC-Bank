package com.example.banksystem.controller;

import com.example.banksystem.model.User;
import com.example.banksystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return only safe fields, avoid exposing passwordHash
        Map<String, String> userData = new HashMap<>();
        userData.put("fullname", user.getFullname());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());

        return ResponseEntity.ok(userData);
    }
}
