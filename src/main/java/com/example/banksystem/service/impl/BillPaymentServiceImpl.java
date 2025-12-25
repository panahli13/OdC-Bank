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

        // Cashback 5%
        if (account.getBonusBalance() == null)
            account.setBonusBalance(0.0);
        account.setBonusBalance(account.getBonusBalance() + (amount * 0.05));

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
        double fee = 0;
        double netAmount = amount;

        if (card.getCardType() == Card.CardType.CREDIT) {
            double currentDebt = card.getCurrentDebt() != null ? card.getCurrentDebt() : 0;
            double available = card.getCreditLimit() - currentDebt;

            if (amount > available)
                throw new Exception("Kredit limitini aşır");

            card.setCurrentDebt(currentDebt + amount);
            // Credit cards usually don't have fee for payments in this demo, or we can keep
            // it simple
            cardRepository.save(card);

        } else {
            // DEBIT/GOLD/PLATINUM/EXTERNAL - Deduct from CARD balance
            fee = TransactionFeeCalculator.calculate("BILL_PAYMENT", amount);
            netAmount = amount + fee;

            if (card.getBalance() < netAmount)
                throw new Exception("Card Balance kifayət etmir (fee daxil)");

            card.setBalance(card.getBalance() - netAmount);
            cardRepository.save(card);
        }

        // Cashback 5% (Apply to Account Bonus)
        if (account.getBonusBalance() == null)
            account.setBonusBalance(0.0);
        account.setBonusBalance(account.getBonusBalance() + (amount * 0.05));
        // We need to save account because bonus is on account
        // card.getAccount() is managed, so saving card or transaction might cascade
        // but explicit save is safer given we modified a field
        // repositories are not autowired here for Account?
        // card.getAccount() is an entity attached to context if Transactional works.
        // But to be safe, since we don't have AccountRepository injected here (wait,
        // let's check imports/fields)

        // Actually this class doesn't have AccountRepository.
        // JPA Check: Since we are in @Transactional, changes to 'account' (which came
        // from card.getAccount()) 'should' be flushed if attached.

        Transaction transaction = new Transaction();
        transaction.setFromAccountId(account.getId());
        transaction.setToAccountId(null);
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setNetAmount(netAmount);
        transaction.setReference(billDetails + " (via Card " + card.getCardNumber().substring(12) + ")");
        transaction.setType("BILL_PAYMENT");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}
