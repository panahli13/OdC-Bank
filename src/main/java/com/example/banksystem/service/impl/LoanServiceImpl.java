package com.example.banksystem.service.impl;

import com.example.banksystem.model.Account;
import com.example.banksystem.entity.Loan;
import com.example.banksystem.entity.Transaction;
import com.example.banksystem.model.TransactionStatus;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.LoanRepository;
import com.example.banksystem.repository.TransactionRepository;
import com.example.banksystem.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private com.example.banksystem.repository.CardRepository cardRepository;

    @Transactional
    @Override
    public Loan requestLoan(Long accountId, Double principal, Double interestRate) throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Account tapılmadı"));

        Loan loan = new Loan();
        loan.setAccountId(accountId);
        loan.setPrincipal(principal);
        loan.setInterestRate(interestRate);
        loan.setTermMonths(12); // default term, lazım gələrsə parametrlə keçə bilərsən
        loan.setOutstandingBalance(principal + principal * interestRate / 100);
        loan.setStatus("PENDING");

        return loanRepository.save(loan);
    }

    @Transactional
    @Override
    public Loan applyLoan(Long accountId, double principal, double interestRate, int termMonths) throws Exception {
        Loan loan = requestLoan(accountId, principal, interestRate);

        loan.setStatus("APPROVED");
        loan.setTermMonths(termMonths);
        loanRepository.save(loan);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Account tapılmadı"));

        account.setBalance(account.getBalance() + principal);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setFromAccountId(null); // Bankdan gəlir
        transaction.setToAccountId(account.getId());
        transaction.setAmount(principal);
        transaction.setFee(0.0);
        transaction.setNetAmount(principal);
        transaction.setType("LOAN_CREDIT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return loan;
    }

    @Transactional
    @Override
    public Loan applyLoanWithCard(Long accountId, Long cardId, double principal, double interestRate, int termMonths)
            throws Exception {
        // 1. Create Base Loan Record
        Loan loan = requestLoan(accountId, principal, interestRate);
        loan.setStatus("APPROVED");
        loan.setTermMonths(termMonths);
        loanRepository.save(loan);

        // 2. Load and Fund Card
        com.example.banksystem.entity.Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new Exception("Card tapılmadı"));

        if (!card.getAccount().getId().equals(accountId)) {
            throw new Exception("Card bu hesaba aid deyil");
        }

        card.setBalance((card.getBalance() != null ? card.getBalance() : 0.0) + principal);
        cardRepository.save(card);

        // 3. Log Transaction
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(null); // Bankdan
        transaction.setToAccountId(accountId);
        transaction.setAmount(principal);
        transaction.setFee(0.0);
        transaction.setNetAmount(principal);
        transaction.setType("LOAN_CREDIT_CARD");
        transaction.setReference("LOAN-" + loan.getId() + "-TO-CARD-" + cardId);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return loan;
    }

    @Transactional
    @Override
    public void makeRepayment(Long loanId, double amount) throws Exception {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new Exception("Loan tapılmadı"));

        Account account = accountRepository.findById(loan.getAccountId())
                .orElseThrow(() -> new Exception("Account tapılmadı"));

        if (account.getBalance() < amount)
            throw new Exception("Balance kifayət etmir");

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        loan.setOutstandingBalance(loan.getOutstandingBalance() - amount);
        if (loan.getOutstandingBalance() <= 0)
            loan.setStatus("REPAID");
        loanRepository.save(loan);

        Transaction transaction = new Transaction();
        transaction.setFromAccountId(account.getId());
        transaction.setToAccountId(null);
        transaction.setAmount(amount);
        transaction.setFee(0.0);
        transaction.setNetAmount(amount);
        transaction.setType("LOAN_REPAYMENT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    public Loan getLoanById(Long loanId) throws Exception {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new Exception("Loan tapılmadı"));
    }

    @Override
    public List<Loan> getLoansByAccount(Long accountId) {
        return loanRepository.findByAccountId(accountId);
    }
}
