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
    private com.example.banksystem.service.AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my-account")
    public ResponseEntity<?> getMyAccount(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User tapılmadı"));
        List<Account> accounts = accountRepository.findByUser_Id(user.getId());
        if (accounts.isEmpty()) {
            return ResponseEntity.badRequest().body("Account tapılmadı");
        }
        return ResponseEntity.ok(accounts.get(0));
    }

    @GetMapping("/my-account/transactions")
    public ResponseEntity<?> getMyTransactions(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User tapılmadı"));
        List<Account> accounts = accountRepository.findByUser_Id(user.getId());
        if (accounts.isEmpty()) {
            return ResponseEntity.badRequest().body("Account tapılmadı");
        }
        Account account = accounts.get(0);
        List<Transaction> transactions = transactionRepository.findByFromAccountIdOrToAccountId(account.getId(),
                account.getId());
        return ResponseEntity.ok(transactions);
    }

    @org.springframework.web.bind.annotation.PostMapping("/my-account/redeem-bonus")
    public ResponseEntity<?> redeemBonus(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User tapılmadı"));
        List<Account> accounts = accountRepository.findByUser_Id(user.getId());
        if (accounts.isEmpty()) {
            return ResponseEntity.badRequest().body("Account tapılmadı");
        }
        Account account = accounts.get(0);
        try {
            Account updated = accountService.redeemBonus(account.getId());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long accountId, Principal principal) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User tapılmadı"));

        if (account.getUser() == null || !account.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

        return ResponseEntity.ok(account.getBalance());
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable Long accountId, Principal principal) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User tapılmadı"));

        if (account.getUser() == null || !account.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

        List<Transaction> transactions = transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);
        return ResponseEntity.ok(transactions);
    }
}
