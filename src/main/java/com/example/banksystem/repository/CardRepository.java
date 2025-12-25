package com.example.banksystem.repository;

import com.example.banksystem.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByAccountId(Long accountId);

    Card findByCardNumber(String cardNumber);

    Card findByCardNumberEndsWith(String suffix);
}
