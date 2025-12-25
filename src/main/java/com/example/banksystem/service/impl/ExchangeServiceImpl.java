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

    @Autowired
    private com.example.banksystem.repository.CardRepository cardRepository;

    @Override
    @Transactional
    public Transaction exchange(Long accountId, String fromCurrency, String toCurrency, double amount)
            throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Hesab tapılmadı: " + accountId));

        if (amount <= 0)
            throw new Exception("Məbləğ 0-dan böyük olmalıdır.");

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

    @Override
    @Transactional
    public Transaction exchangeWithCard(Long accountId, Long cardId, String fromCurrency, String toCurrency,
            double amount) throws Exception {
        com.example.banksystem.entity.Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new Exception("Card tapılmadı"));

        if (!card.getAccount().getId().equals(accountId)) {
            throw new Exception("Card bu hesaba aid deyil");
        }

        if (amount <= 0)
            throw new Exception("Məbləğ 0-dan böyük olmalıdır.");

        double fee = TransactionFeeCalculator.calculate("TRANSFER", amount);
        double totalDebit = amount + fee;

        // 1. DEDUCT from Source Balance
        if ("AZN".equals(fromCurrency)) {
            if ((card.getBalance() == null ? 0 : card.getBalance()) < totalDebit)
                throw new Exception("AZN Balansı kifayət etmir");
            card.setBalance(card.getBalance() - totalDebit);
        } else if ("USD".equals(fromCurrency)) {
            if ((card.getBalanceUsd() == null ? 0 : card.getBalanceUsd()) < totalDebit)
                throw new Exception("USD Balansı kifayət etmir");
            card.setBalanceUsd(card.getBalanceUsd() - totalDebit);
        } else if ("EUR".equals(fromCurrency)) {
            if ((card.getBalanceEur() == null ? 0 : card.getBalanceEur()) < totalDebit)
                throw new Exception("EUR Balansı kifayət etmir");
            card.setBalanceEur(card.getBalanceEur() - totalDebit);
        } else {
            throw new Exception("Unsupported Source Currency");
        }

        double convertedAmount = exchangeRateService.convert(fromCurrency, toCurrency, amount);

        // 2. CREDIT to Target Balance
        if ("AZN".equals(toCurrency)) {
            card.setBalance((card.getBalance() == null ? 0 : card.getBalance()) + convertedAmount);
        } else if ("USD".equals(toCurrency)) {
            card.setBalanceUsd((card.getBalanceUsd() == null ? 0 : card.getBalanceUsd()) + convertedAmount);
        } else if ("EUR".equals(toCurrency)) {
            card.setBalanceEur((card.getBalanceEur() == null ? 0 : card.getBalanceEur()) + convertedAmount);
        } else {
            throw new Exception("Unsupported Target Currency");
        }

        cardRepository.save(card);

        Transaction transaction = new Transaction();
        transaction.setReference("EXCHANGE-CARD-" + System.currentTimeMillis());
        transaction.setType("CURRENCY_EXCHANGE_CARD");
        transaction.setAmount(amount);
        transaction.setFromAccountId(accountId); // Linked Account
        transaction.setToAccountId(accountId);
        transaction.setFee(fee);
        transaction.setNetAmount(convertedAmount - fee);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return transaction;
    }
}
