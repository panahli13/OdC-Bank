package com.example.banksystem.service.impl;

import com.example.banksystem.model.Account;
import com.example.banksystem.entity.Card;
import com.example.banksystem.entity.Transaction;
import com.example.banksystem.model.TransactionStatus;
import com.example.banksystem.repository.CardRepository;
import com.example.banksystem.repository.TransactionRepository;
import com.example.banksystem.service.BillPaymentService;
import com.example.banksystem.util.TransactionFeeCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    @Override
    @Transactional
    public Transaction performBillPayment(Account account, double amount, String billDetails) throws Exception {
        double fee = TransactionFeeCalculator.calculate("BILL_PAYMENT", amount);
        double netAmount = amount + fee;

        if (account.getBalance() < netAmount)
            throw new Exception("Balance kifayət etmir (fee daxil)");

        account.setBalance(account.getBalance() - netAmount);

        Transaction transaction = new Transaction();
        transaction.setFromAccountId(account.getId());
        transaction.setToAccountId(null);
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setNetAmount(netAmount);
        transaction.setReference(billDetails);
        transaction.setType("BILL_PAYMENT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction performBillPaymentByCard(Long cardId, double amount, String billDetails) throws Exception {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new Exception("Card tapılmadı"));

        Account account = card.getAccount();

        if (card.getCardType() == Card.CardType.CREDIT) {
            double currentDebt = card.getCurrentDebt() != null ? card.getCurrentDebt() : 0;
            double available = card.getCreditLimit() - currentDebt;

            if (amount > available)
                throw new Exception("Kredit limitini aşır");

            card.setCurrentDebt(currentDebt + amount);
            cardRepository.save(card);

            // Kredit kartında balansdan çıxılmır
        } else {
            double fee = TransactionFeeCalculator.calculate("BILL_PAYMENT", amount);
            double netAmount = amount + fee;

            if (account.getBalance() < netAmount)
                throw new Exception("Balance kifayət etmir (fee daxil)");

            account.setBalance(account.getBalance() - netAmount);
        }

        Transaction transaction = new Transaction();
        transaction.setFromAccountId(account.getId());
        transaction.setToAccountId(null);
        transaction.setAmount(amount);
        transaction.setFee(card.getCardType() == Card.CardType.CREDIT ? 0 : TransactionFeeCalculator.calculate("BILL_PAYMENT", amount));
        transaction.setNetAmount(card.getCardType() == Card.CardType.CREDIT ? amount : amount + TransactionFeeCalculator.calculate("BILL_PAYMENT", amount));
        transaction.setReference(billDetails);
        transaction.setType("BILL_PAYMENT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}
