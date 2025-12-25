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
    private String cvv;

    @Enumerated(EnumType.STRING)
    private CardType cardType; // DEBIT, CREDIT, CASHBACK

    private boolean active;

    private Double creditLimit;
    private Double currentDebt;

    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    private Double balance = 0.0; // AZN

    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    private Double balanceUsd = 0.0;

    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    private Double balanceEur = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public enum CardType {
        DEBIT,
        CREDIT,
        CASHBACK,
        EXTERNAL,
        GOLD,
        PLATINUM
    }
}
