package com.example.banksystem.service;

import com.example.banksystem.dto.LoginDto;
import com.example.banksystem.dto.RegisterDto;
import com.example.banksystem.model.Account;
import com.example.banksystem.model.AccountType;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.UserRepository;
import com.example.banksystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Autowired
    private AccountRepository accountRepository;

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            accountNumber = String.format("%08d", random.nextInt(100000000));
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }

    public void register(RegisterDto dto) {
        // Check if user exists
        java.util.Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());

        if (existingUser.isPresent()) {
            User u = existingUser.get();
            if (u.getEnabled()) {
                throw new RuntimeException("Email already in use");
            } else {
                // User exists but not verified. Resend code.
                resendVerificationCode(u.getEmail());
                return;
            }
        }

        User user = new User();
        user.setFullname(dto.getFullname());
        user.setFincode(dto.getFincode());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(false);

        // Generate Verification Code (6 digits)
        generateAndSendCode(user);

        userRepository.save(user);

        // Create account logic moved to AFTER verification (optional, but safer)
        // For now keep here for simplicity or move to verifyEmail?
        // Let's keep account creation here but it will be inactive effectively since
        // user is disabled.
        createInitialAccount(user);
    }

    private void generateAndSendCode(User user) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setVerificationCode(code);
        user.setVerificationCodeExpiresAt(java.time.LocalDateTime.now().plusMinutes(15)); // Expires in 15 mins

        emailService.sendVerificationEmail(user.getEmail(), code);
    }

    private void createInitialAccount(User user) {
        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(0.0);
        account.setAccountType(AccountType.CHECKING);
        account.setStatus("ACTIVE");
        account.setDailyWithdrawalLimit(500.0);
        account.setMinBalance(0.0);
        accountRepository.save(account);
    }

    public void verifyEmail(String code) {
        User user = userRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid verification code"));

        if (user.getVerificationCodeExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Verification code expired");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEnabled()) {
            throw new RuntimeException("User is already verified");
        }

        generateAndSendCode(user);
        userRepository.save(user);
    }

    public String login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEnabled()) {
            throw new RuntimeException("Account is not verified");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Incorrect password");

        return jwtUtil.generateToken(user.getEmail());
    }
}
