package com.example.banksystem.entity;

import com.example.banksystem.model.Account;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "savings_goals")
public class SavingsGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double targetAmount;

    private Double currentAmount = 0.0;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public enum GoalStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }
}
