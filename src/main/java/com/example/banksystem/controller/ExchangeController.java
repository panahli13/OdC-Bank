package com.example.banksystem.controller;

import com.example.banksystem.model.Account;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.UserRepository;
import com.example.banksystem.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> exchangeCurrency(
            @RequestParam Long accountId,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam double amount,
            Principal principal
    ) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
            User user = userRepository.findByFullname(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tapılmadı"));

            if (account.getUser() == null || !account.getUser().getId().equals(user.getId()))
                return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

            return ResponseEntity.ok(exchangeService.exchange(accountId, fromCurrency, toCurrency, amount));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xəta: " + e.getMessage());
        }
    }
}
