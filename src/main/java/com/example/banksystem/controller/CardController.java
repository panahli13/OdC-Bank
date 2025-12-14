package com.example.banksystem.controller;

import com.example.banksystem.model.Account;
import com.example.banksystem.entity.Card;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.UserRepository;
import com.example.banksystem.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createCard(
            @RequestParam Long accountId,
            @RequestParam String cardType,
            Principal principal
    ) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
            User user = userRepository.findByFullname(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tapılmadı"));

            if (!account.getUserId().equals(user.getId()))
                return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

            Card card = cardService.createCard(accountId, cardType);
            return ResponseEntity.ok(card);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xəta: " + e.getMessage());
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getCardsByAccount(@PathVariable Long accountId, Principal principal) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
            User user = userRepository.findByFullname(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tapılmadı"));

            if (!account.getUserId().equals(user.getId()))
                return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

            List<Card> cards = cardService.getCardsByAccount(accountId);
            return ResponseEntity.ok(cards);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xəta: " + e.getMessage());
        }
    }
}
