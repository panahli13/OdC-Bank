package com.example.banksystem.service;

import com.example.banksystem.entity.Loan;

import java.util.List;

public interface LoanService {
    Loan requestLoan(Long accountId, Double principal, Double interestRate) throws Exception;

    Loan getLoanById(Long loanId) throws Exception;

    Loan applyLoan(Long accountId, double principal, double interestRate, int termMonths) throws Exception;

    Loan applyLoanWithCard(Long accountId, Long cardId, double principal, double interestRate, int termMonths)
            throws Exception;

    void makeRepayment(Long loanId, double amount) throws Exception;

    List<Loan> getLoansByAccount(Long accountId);
}
