package com.example.banksystem.service.impl;

import com.example.banksystem.entity.Card;
import com.example.banksystem.entity.SavingsGoal;
import com.example.banksystem.entity.Transaction;
import com.example.banksystem.model.Account;
import com.example.banksystem.model.TransactionStatus;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.CardRepository;
import com.example.banksystem.repository.SavingsGoalRepository;
import com.example.banksystem.repository.TransactionRepository;
import com.example.banksystem.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavingsGoalServiceImpl implements SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    @Override
    public SavingsGoal createGoal(Long accountId, String name, Double targetAmount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        SavingsGoal goal = new SavingsGoal();
        goal.setAccount(account);
        goal.setName(name);
        goal.setTargetAmount(targetAmount);
        goal.setCurrentAmount(0.0);
        goal.setStatus(SavingsGoal.GoalStatus.ACTIVE);

        return savingsGoalRepository.save(goal);
    }

    @Override
    @Transactional
    public SavingsGoal depositToGoal(Long goalId, Double amount, Long fromCardId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (fromCardId != null) {
            // Deduct from Card
            Card card = cardRepository.findById(fromCardId)
                    .orElseThrow(() -> new RuntimeException("Card not found"));

            if (card.getBalance() < amount) {
                throw new RuntimeException("Insufficient card funds");
            }
            card.setBalance(card.getBalance() - amount);
            cardRepository.save(card);
        } else {
            // Deduct from Main Account
            Account account = goal.getAccount();
            if (account.getBalance() < amount) {
                throw new RuntimeException("Insufficient account funds");
            }
            account.setBalance(account.getBalance() - amount);
            accountRepository.save(account);
        }

        // Add to Goal
        goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        if (goal.getCurrentAmount() >= goal.getTargetAmount()) {
            goal.setStatus(SavingsGoal.GoalStatus.COMPLETED);
        }
        SavingsGoal savedGoal = savingsGoalRepository.save(goal);

        // Record Transaction
        Transaction tx = new Transaction();
        tx.setFromAccountId(goal.getAccount().getId());
        tx.setType("SAVINGS_DEPOSIT");
        tx.setAmount(amount);
        tx.setReference("Deposit to Goal: " + goal.getName() + (fromCardId != null ? " (Card)" : ""));
        tx.setCreatedAt(LocalDateTime.now());
        tx.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(tx);

        return savedGoal;
    }

    @Override
    @Transactional
    public void deleteGoal(Long goalId, Long refundCardId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (goal.getCurrentAmount() > 0) {
            String referenceSuffix = "";
            if (refundCardId != null) {
                // Refund to Card
                Card card = cardRepository.findById(refundCardId)
                        .orElseThrow(() -> new RuntimeException("Card not found"));
                card.setBalance(card.getBalance() + goal.getCurrentAmount());
                cardRepository.save(card);
                referenceSuffix = " (to Card)";
            } else {
                // Refund to Main Account
                Account account = goal.getAccount();
                account.setBalance(account.getBalance() + goal.getCurrentAmount());
                accountRepository.save(account);
            }

            // Record Refund Transaction
            Transaction tx = new Transaction();
            tx.setFromAccountId(goal.getAccount().getId());
            tx.setType("SAVINGS_REFUND");
            tx.setAmount(goal.getCurrentAmount());
            tx.setReference("Refund from Goal: " + goal.getName() + referenceSuffix);
            tx.setCreatedAt(LocalDateTime.now());
            tx.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(tx);
        }

        savingsGoalRepository.delete(goal);
    }

    @Override
    public List<SavingsGoal> getGoalsByAccount(Long accountId) {
        return savingsGoalRepository.findByAccountId(accountId);
    }
}
