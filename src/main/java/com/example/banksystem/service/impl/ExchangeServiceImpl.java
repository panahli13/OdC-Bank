package com.example.banksystem.service.impl;

import com.example.banksystem.model.Account;
import com.example.banksystem.entity.Transaction;
import com.example.banksystem.model.TransactionStatus;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.TransactionRepository;
import com.example.banksystem.service.ExchangeService;
import com.example.banksystem.service.ExchangeRateService;
import com.example.banksystem.util.TransactionFeeCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Override
    @Transactional
    public Transaction exchange(Long accountId, String fromCurrency, String toCurrency, double amount) throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Hesab tapılmadı: " + accountId));

        if (amount <= 0) throw new Exception("Məbləğ 0-dan böyük olmalıdır.");

        double fee = TransactionFeeCalculator.calculate("TRANSFER", amount);
        double totalDebit = amount + fee;

        if (account.getBalance() < totalDebit)
            throw new Exception("Balans kifayət etmir (amount + fee).");

        double convertedAmount = exchangeRateService.convert(fromCurrency, toCurrency, amount);

        account.setBalance(account.getBalance() - totalDebit + convertedAmount); // yeni balans
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setReference("EXCHANGE-" + System.currentTimeMillis());
        transaction.setType("CURRENCY_EXCHANGE");
        transaction.setAmount(amount);
        transaction.setFromAccountId(accountId);
        transaction.setToAccountId(accountId);
        transaction.setFee(fee);
        transaction.setNetAmount(convertedAmount - fee);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return transaction;
    }
}
