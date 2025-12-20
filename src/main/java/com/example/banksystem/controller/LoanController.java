package com.example.banksystem.controller;

import com.example.banksystem.model.Account;
import com.example.banksystem.entity.Loan;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.UserRepository;
import com.example.banksystem.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLoan(
            @RequestParam Long accountId,
            @RequestParam double principal,
            @RequestParam double interestRate,
            @RequestParam int termMonths,
            Principal principalUser
    ) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
            User user = userRepository.findByFullname(principalUser.getName())
                    .orElseThrow(() -> new RuntimeException("User tapılmadı"));

            if (account.getUser() == null || !account.getUser().getId().equals(user.getId()))
                return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

            Loan loan = loanService.applyLoan(accountId, principal, interestRate, termMonths);
            return ResponseEntity.ok(loan);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xəta: " + e.getMessage());
        }
    }

    @PostMapping("/repay")
    public ResponseEntity<?> repayLoan(
            @RequestParam Long loanId,
            @RequestParam double amount,
            Principal principalUser
    ) {
        try {
            Loan loan = loanService.getLoanById(loanId);
            Account account = accountRepository.findById(loan.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
            User user = userRepository.findByFullname(principalUser.getName())
                    .orElseThrow(() -> new RuntimeException("User tapılmadı"));

            if (account.getUser() == null || !account.getUser().getId().equals(user.getId()))
                return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

            loanService.makeRepayment(loanId, amount);

            return ResponseEntity.ok("Ödəniş uğurla edildi.");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xəta: " + e.getMessage());
        }
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<?> loanHistory(@PathVariable Long accountId, Principal principalUser) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account tapılmadı"));
            User user = userRepository.findByFullname(principalUser.getName())
                    .orElseThrow(() -> new RuntimeException("User tapılmadı"));

            if (account.getUser() == null || !account.getUser().getId().equals(user.getId()))
                return ResponseEntity.status(403).body("Bu hesab üzrə icazəniz yoxdur");

            List<Loan> loans = loanService.getLoansByAccount(accountId);
            return ResponseEntity.ok(loans);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xəta: " + e.getMessage());
        }
    }
}
