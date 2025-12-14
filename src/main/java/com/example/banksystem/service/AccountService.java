package com.example.banksystem.service;

import com.example.banksystem.model.Account;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public Account createAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setUser(user);
        account.setBalance(0.0);
        account.setAccountNumber(generateAccountNumber());

        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        Random rand = new Random();
        return "AZ" + (10000000 + rand.nextInt(90000000));
    }
}
