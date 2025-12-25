package com.example.banksystem.service.impl;

import com.example.banksystem.entity.*;
import com.example.banksystem.model.Account;
import com.example.banksystem.model.User;
import com.example.banksystem.repository.AccountRepository;
import com.example.banksystem.repository.CardRepository;
import com.example.banksystem.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Card createCard(Long accountId, String cardType) throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Account tapılmadı"));

        Card.CardType typeEnum = Card.CardType.valueOf(cardType.toUpperCase());

        // Check if user already has this card type
        List<Card> existingCards = cardRepository.findByAccountId(accountId);
        for (Card c : existingCards) {
            if (c.getCardType() == typeEnum && c.isActive()) {
                throw new Exception("Siz artıq " + typeEnum + " kartına sahibsiniz.");
            }
        }

        Card card = new Card();
        card.setAccount(account);
        card.setCardType(typeEnum);
        // Generate 16 digit random number
        long random12 = (long) (Math.random() * 1_000_000_000_000L);
        String number = "4169" + String.format("%012d", random12);
        card.setCardNumber(number);

        // Generate 3 digit CVV
        int randomCvv = (int) (Math.random() * 900) + 100;
        card.setCvv(String.valueOf(randomCvv));

        // Demo: Initialize with random balance for variety (All cards)
        double initialBalance = Math.floor(Math.random() * 5000) + 100; // 100 to 5100
        card.setBalance(initialBalance);

        card.setActive(true);

        return cardRepository.save(card);
    }

    @Override
    public Card addExternalCard(Long accountId, String cardNumber, String cardType) throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Account tapılmadı"));

        // Basic validation
        if (cardNumber == null || cardNumber.length() < 16) {
            throw new Exception("Yanlış kart nömrəsi");
        }

        Card card = new Card();
        card.setAccount(account);
        card.setCardType(Card.CardType.valueOf(cardType.toUpperCase())); // EXTERNAL
        card.setCardNumber(cardNumber);

        // Generate random 3 digit CVV for external too (simulation)
        int randomCvv = (int) (Math.random() * 900) + 100;
        card.setCvv(String.valueOf(randomCvv));

        card.setBalance(Math.floor(Math.random() * 10000)); // Mock balance for external
        card.setActive(true);

        return cardRepository.save(card);
    }

    @Override
    public List<Card> getCardsByAccount(Long accountId) throws Exception {
        if (!accountRepository.existsById(accountId))
            throw new Exception("Account tapılmadı");

        List<Card> cards = cardRepository.findByAccountId(accountId);
        // Lazy persistence for missing CVVs (Migration for existing cards)
        boolean changed = false;
        for (Card c : cards) {
            if (c.getCvv() == null || c.getCvv().isEmpty() || c.getCvv().equals("000")) {
                int randomCvv = (int) (Math.random() * 900) + 100;
                c.setCvv(String.valueOf(randomCvv));
                cardRepository.save(c);
                changed = true;
            }
        }

        if (changed) {
            cards = cardRepository.findByAccountId(accountId);
        }

        return cards;
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void deleteCard(Long cardId, String email, String password) throws Exception {
        if (!cardRepository.existsById(cardId)) {
            throw new Exception("Card tapılmadı");
        }

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new Exception("Card tapılmadı"));
        Account account = card.getAccount();
        User user = account.getUser();

        if (user == null)
            throw new Exception("Card sahibi tapılmadı");

        // Validate Email
        if (!user.getEmail().equalsIgnoreCase(email)) {
            throw new Exception("Email yanlışdır bu kart üçün.");
        }

        // Validate Password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new Exception("Şifrə yanlışdır.");
        }

        cardRepository.deleteById(cardId);
    }
}
