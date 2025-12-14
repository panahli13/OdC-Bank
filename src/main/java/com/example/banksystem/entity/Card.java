package com.example.banksystem.entity;

import com.example.banksystem.model.Account;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;

    @Enumerated(EnumType.STRING)
    private CardType cardType; // DEBIT, CREDIT, CASHBACK

    private boolean active;

    private Double creditLimit;
    private Double currentDebt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public enum CardType {
        DEBIT,
        CREDIT,
        CASHBACK
    }
}
