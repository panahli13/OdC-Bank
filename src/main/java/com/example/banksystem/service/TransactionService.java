package com.example.banksystem.service;

import com.example.banksystem.entity.Transaction;
import com.example.banksystem.model.Account;
import com.example.banksystem.model.TransactionStatus;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.TransactionRepository;
import com.example.banksystem.util.TransactionFeeCalculator;
import com.example.banksystem.util.TransactionReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    public String deposit(Long accountId, Double amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Deposit amount must be greater than 0");
        }

        Transaction t = new Transaction();
        t.setReference(TransactionReferenceGenerator.generate());
        t.setType("DEPOSIT");
        t.setAmount(amount);
        t.setFee(0.0);
        t.setNetAmount(amount);
        t.setToAccountId(accountId);
        t.setCreatedAt(LocalDateTime.now());
        t.setStatus(TransactionStatus.PENDING);

        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            checkAccountStatus(account);

            account.setBalance(account.getBalance() + amount);
            accountRepository.save(account);

            t.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(t);

            return "Deposit successful. Reference: " + t.getReference();

        } catch (Exception e) {
            t.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(t);
            throw e;
        }
    }


    public String withdraw(Long accountId, Double amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than 0");
        }

        Transaction t = new Transaction();
        t.setReference(TransactionReferenceGenerator.generate());
        t.setType("WITHDRAW");
        t.setAmount(amount);
        t.setFromAccountId(accountId);
        t.setCreatedAt(LocalDateTime.now());
        t.setStatus(TransactionStatus.PENDING);

        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            checkAccountStatus(account);

            double fee = TransactionFeeCalculator.calculate("WITHDRAW", amount);
            double totalAmount = amount + fee;

            double newBalance = account.getBalance() - totalAmount;
            if (newBalance < account.getMinBalance()) {
                throw new RuntimeException("Cannot withdraw below minimum balance");
            }

            double withdrawnToday =
                    transactionRepository.getWithdrawnAmountToday(accountId, LocalDate.now());
            if (withdrawnToday + amount > account.getDailyWithdrawalLimit()) {
                throw new RuntimeException("Daily withdrawal limit exceeded");
            }

            account.setBalance(newBalance);
            accountRepository.save(account);

            t.setFee(fee);
            t.setNetAmount(amount);
            t.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(t);

            return "Withdrawal successful. Reference: " + t.getReference();

        } catch (Exception e) {
            t.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(t);
            throw e;
        }
    }


    public String transfer(Long fromId, Long toId, Double amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Transfer amount must be greater than 0");
        }

        Transaction t = new Transaction();
        t.setReference(TransactionReferenceGenerator.generate());
        t.setType("TRANSFER");
        t.setAmount(amount);
        t.setFromAccountId(fromId);
        t.setToAccountId(toId);
        t.setCreatedAt(LocalDateTime.now());
        t.setStatus(TransactionStatus.PENDING);

        try {
            Account from = accountRepository.findById(fromId)
                    .orElseThrow(() -> new RuntimeException("From account not found"));

            Account to = accountRepository.findById(toId)
                    .orElseThrow(() -> new RuntimeException("To account not found"));

            checkAccountStatus(from);
            checkAccountStatus(to);

            double fee = TransactionFeeCalculator.calculate("TRANSFER", amount);
            double totalAmount = amount + fee;

            double newBalance = from.getBalance() - totalAmount;
            if (newBalance < from.getMinBalance()) {
                throw new RuntimeException("Cannot transfer below minimum balance");
            }

            double withdrawnToday =
                    transactionRepository.getWithdrawnAmountToday(fromId, LocalDate.now());
            if (withdrawnToday + amount > from.getDailyWithdrawalLimit()) {
                throw new RuntimeException("Daily withdrawal limit exceeded");
            }

            from.setBalance(newBalance);
            to.setBalance(to.getBalance() + amount);

            accountRepository.save(from);
            accountRepository.save(to);

            t.setFee(fee);
            t.setNetAmount(amount);
            t.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(t);

            return "Transfer successful. Reference: " + t.getReference();

        } catch (Exception e) {
            t.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(t);
            throw e;
        }
    }


    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository
                .findByFromAccountIdOrToAccountId(accountId, accountId)
                .stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    private void checkAccountStatus(Account account) {
        if (!account.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Account is not active");
        }
    }
}
