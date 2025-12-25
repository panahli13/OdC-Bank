package com.example.banksystem.controller;

import com.example.banksystem.entity.SavingsGoal;
import com.example.banksystem.model.Account;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.UserRepository;
import com.example.banksystem.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/savings")
public class SavingsGoalController {

    @Autowired
    private SavingsGoalService savingsGoalService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/my-goals")
    public ResponseEntity<?> getMyGoals(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find main account
        Account account = accountRepository.findByUser_Id(user.getId()).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return ResponseEntity.ok(savingsGoalService.getGoalsByAccount(account.getId()));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGoal(@RequestParam String name, @RequestParam Double target, Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Account account = accountRepository.findByUser_Id(user.getId()).stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            SavingsGoal goal = savingsGoalService.createGoal(account.getId(), name, target);
            return ResponseEntity.ok(goal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestParam Long goalId,
            @RequestParam Double amount,
            @RequestParam(required = false) Long cardId) {
        try {
            SavingsGoal goal = savingsGoalService.depositToGoal(goalId, amount, cardId);
            return ResponseEntity.ok(goal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteGoal(@RequestParam Long goalId, @RequestParam(required = false) Long refundCardId) {
        try {
            savingsGoalService.deleteGoal(goalId, refundCardId);
            return ResponseEntity.ok("Goal deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
