package com.example.banksystem.controller;

import com.example.banksystem.model.Account;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.UserRepository;
import com.example.banksystem.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/interest")
public class InterestController {

    @Autowired
    private InterestService interestService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/apply")
    public ResponseEntity<?> applyInterest(
            @RequestParam Long accountId,
            @RequestParam double interestRate,
            Principal principal
    ) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
            User user = userRepository.findByFullname(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tapılmadı"));

            if (!account.getUserId().equals(user.getId()))
                return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

            interestService.applyInterest(account, interestRate);
            return ResponseEntity.ok("Faiz uğurla əlavə olundu");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xəta: " + e.getMessage());
        }
    }
}
