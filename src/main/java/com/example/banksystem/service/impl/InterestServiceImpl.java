package com.example.banksystem.service.impl;

import com.example.banksystem.model.Account;
import com.example.banksystem.entity.Transaction;
import com.example.banksystem.model.TransactionStatus;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.TransactionRepository;
import com.example.banksystem.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InterestServiceImpl implements InterestService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void applyInterest(Account account, double interestRate) {
        double interest = account.getBalance() * interestRate / 100.0;
        account.setBalance(account.getBalance() + interest);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setReference("INTEREST-" + System.currentTimeMillis());
        transaction.setType("INTEREST_CREDIT");
        transaction.setAmount(interest);
        transaction.setFromAccountId(null);
        transaction.setToAccountId(account.getId());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setFee(0.0);
        transaction.setNetAmount(interest);

        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?") // Hər ayın 1-də gecə 00:00
    public void applyInterestForAllAccounts(double interestRate) {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            if ("ACTIVE".equals(account.getStatus())) {
                applyInterest(account, interestRate);
            }
        }
    }
}
