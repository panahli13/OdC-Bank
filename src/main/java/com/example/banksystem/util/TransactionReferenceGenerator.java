package com.example.banksystem.util;

import java.time.LocalDate;
import java.util.UUID;

public class TransactionReferenceGenerator {

    public static String generate() {
        return "TXN-" +
                LocalDate.now().toString().replace("-", "") +
                "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
