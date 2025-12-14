package com.example.banksystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;
    private Double principal;
    private Double interestRate;
    private Integer termMonths;
    private Double outstandingBalance;
    private String status; // APPROVED, REPAID, DEFAULTED
}
