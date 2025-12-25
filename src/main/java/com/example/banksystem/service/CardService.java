package com.example.banksystem.service;

import com.example.banksystem.entity.Card;

import java.util.List;

public interface CardService {
    Card createCard(Long accountId, String cardType) throws Exception;

    Card addExternalCard(Long accountId, String cardNumber, String cardType) throws Exception;

    List<Card> getCardsByAccount(Long accountId) throws Exception;

    void deleteCard(Long cardId, String email, String password) throws Exception;
}
