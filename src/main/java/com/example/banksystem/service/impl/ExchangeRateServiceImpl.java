package com.example.banksystem.service.impl;

import com.example.banksystem.service.ExchangeRateService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final Map<String, Double> rates = new HashMap<>();

    public ExchangeRateServiceImpl() {
        // Sadə nümunə üçün statik exchange rate
        rates.put("USD", 1.0);
        rates.put("EUR", 0.9);
        rates.put("AZN", 1.7);
    }

    @Override
    public double convert(String fromCurrency, String toCurrency, double amount) throws Exception {
        if (!rates.containsKey(fromCurrency) || !rates.containsKey(toCurrency))
            throw new Exception("Valyuta tapılmadı.");

        double usdAmount = amount / rates.get(fromCurrency);
        return usdAmount * rates.get(toCurrency);
    }
}
