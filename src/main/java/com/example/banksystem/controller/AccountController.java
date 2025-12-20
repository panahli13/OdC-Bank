package com.example.banksystem.controller;

import com.example.banksystem.entity.Transaction;
import com.example.banksystem.model.Account;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.TransactionRepository;
import com.example.banksystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long accountId, Principal principal) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
        User user = userRepository.findByFullname(principal.getName())
                .orElseThrow(() -> new RuntimeException("User tapılmadı"));

        if (account.getUser() == null || !account.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

        return ResponseEntity.ok(account.getBalance());
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable Long accountId, Principal principal) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
        User user = userRepository.findByFullname(principal.getName())
                .orElseThrow(() -> new RuntimeException("User tapılmadı"));

        if (account.getUser() == null || !account.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

        List<Transaction> transactions = transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);
        return ResponseEntity.ok(transactions);
    }
}
