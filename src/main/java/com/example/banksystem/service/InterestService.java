package com.example.banksystem.service;

import com.example.banksystem.model.Account;

public interface InterestService {
    void applyInterest(Account account, double interestRate);
    void applyInterestForAllAccounts(double interestRate);
}
