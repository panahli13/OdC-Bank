package com.example.banksystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    private String accountNumber;
    private Double balance;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private String status;

    private Double dailyWithdrawalLimit;
    private Double minBalance;

    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    private Double bonusBalance = 0.0;
}
