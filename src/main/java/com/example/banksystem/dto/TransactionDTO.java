package com.example.banksystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDTO {
    private String type;
    private Double amount;
    private Long fromAccount;
    private Long toAccount;
    private LocalDateTime date;
}
