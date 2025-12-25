package com.example.banksystem.config;

import com.example.banksystem.entity.Card;
import com.example.banksystem.model.Account;
import com.example.banksystem.model.AccountType;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.CardRepository;
import com.example.banksystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("No users found. Seeding initial data...");

            // Create User
            User user = new User();
            user.setFullname("Test User");
            user.setFincode("1234567");
            user.setEmail("test@bank.com");
            user.setPhone("+994500000000");
            user.setPasswordHash(passwordEncoder.encode("1234")); // Password: 1234
            user.setEnabled(true);
            user = userRepository.save(user);

            // Create Account
            Account account = new Account();
            account.setUser(user);
            account.setAccountNumber("AZ12345678");
            account.setBalance(1000.00);
            account.setAccountType(AccountType.CHECKING);
            account.setStatus("ACTIVE");
            account.setDailyWithdrawalLimit(500.0);
            account.setMinBalance(0.0);
            account = accountRepository.save(account);

            // Create Card
            Card card = new Card();
            card.setAccount(account);
            card.setCardNumber("4169738812345678");
            card.setCardType(Card.CardType.DEBIT);
            card.setActive(true);
            card.setCreditLimit(0.0);
            card.setCurrentDebt(0.0);
            cardRepository.save(card);

            System.out.println("Data seeding completed: User 'test@bank.com' created.");
        }
    }
}
