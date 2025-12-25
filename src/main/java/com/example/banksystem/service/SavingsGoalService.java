package com.example.banksystem.service;

import com.example.banksystem.entity.SavingsGoal;

import java.util.List;

public interface SavingsGoalService {
    SavingsGoal createGoal(Long accountId, String name, Double targetAmount);

    SavingsGoal depositToGoal(Long goalId, Double amount, Long fromCardId);

    void deleteGoal(Long goalId, Long refundCardId);

    List<SavingsGoal> getGoalsByAccount(Long accountId);
}
