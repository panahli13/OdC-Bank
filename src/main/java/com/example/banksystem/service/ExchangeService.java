package com.example.banksystem.service;

import com.example.banksystem.entity.Transaction;

public interface ExchangeService {
    Transaction exchange(Long accountId, String fromCurrency, String toCurrency, double amount) throws Exception;

    Transaction exchangeWithCard(Long accountId, Long cardId, String fromCurrency, String toCurrency, double amount)
            throws Exception;
}
