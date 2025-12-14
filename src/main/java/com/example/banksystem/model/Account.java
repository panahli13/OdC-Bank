package com.example.banksystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    private String accountNumber;
    private Double balance;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private String status;

    private Double dailyWithdrawalLimit;
    private Double minBalance;
}
