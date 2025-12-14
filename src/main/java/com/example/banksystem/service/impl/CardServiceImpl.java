package com.example.banksystem.service.impl;

import com.example.banksystem.entity.*;
import com.example.banksystem.model.Account;
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

        Card card = new Card();
        card.setAccount(account);
        card.setCardType(Card.CardType.valueOf(cardType.toUpperCase()));
        card.setCardNumber("CARD-" + System.currentTimeMillis());
        card.setActive(true);

        return cardRepository.save(card);
    }

    @Override
    public List<Card> getCardsByAccount(Long accountId) throws Exception {
        if (!accountRepository.existsById(accountId))
            throw new Exception("Account tapılmadı");
        return cardRepository.findByAccountId(accountId);
    }
}
