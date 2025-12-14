package com.example.banksystem.util;

public class TransactionFeeCalculator {

    public static double calculate(String type, double amount) {
        return switch (type) {
            case "WITHDRAW" -> amount * 0.005;
            case "TRANSFER" -> amount * 0.01;
            default -> 0.0;
        };
    }
}
