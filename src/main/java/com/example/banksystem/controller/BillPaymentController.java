package com.example.banksystem.controller;

import com.example.banksystem.entity.Transaction;
import com.example.banksystem.model.Account;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.UserRepository;
import com.example.banksystem.repository.CardRepository;
import com.example.banksystem.service.BillPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/bill-payment")
public class BillPaymentController {

    @Autowired
    private BillPaymentService billService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @PostMapping
    public ResponseEntity<?> payBill(
            @RequestParam Long accountId,
            @RequestParam(required = false) Long cardId,
            @RequestParam Double amount,
            @RequestParam String billDetails,
            Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User tapılmadı"));

            if (cardId != null && cardId > 0) {
                // Pay with Card
                com.example.banksystem.entity.Card card = cardRepository.findById(cardId)
                        .orElseThrow(() -> new RuntimeException("Card tapılmadı"));

                if (card.getAccount().getUser().getId() != user.getId()) {
                    return ResponseEntity.status(403).body("Bu kart üzrə icazəniz yoxdur");
                }

                Transaction transaction = billService.performBillPaymentByCard(cardId, amount, billDetails);
                return ResponseEntity.ok(transaction);
            }

            // Pay with Main Account
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account tapılmadı"));

            if (account.getUser() == null || !account.getUser().getId().equals(user.getId()))
                return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

            Transaction transaction = billService.performBillPayment(account, amount, billDetails);
            return ResponseEntity.ok(transaction);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xəta: " + e.getMessage());
        }
    }
}
