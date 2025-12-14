package com.example.banksystem.service;

public interface ExchangeRateService {
    double convert(String fromCurrency, String toCurrency, double amount) throws Exception;
}
