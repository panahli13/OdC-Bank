package com.example.banksystem.service;

import com.example.banksystem.entity.Card;

import java.util.List;

public interface CardService {
    Card createCard(Long accountId, String cardType) throws Exception;
    List<Card> getCardsByAccount(Long accountId) throws Exception;
}
