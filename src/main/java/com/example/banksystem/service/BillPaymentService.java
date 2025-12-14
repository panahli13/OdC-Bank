package com.example.banksystem.service;

import com.example.banksystem.model.Account;
import com.example.banksystem.entity.Transaction;

public interface BillPaymentService {
    Transaction performBillPayment(Account account, double amount, String billDetails) throws Exception;
    Transaction performBillPaymentByCard(Long cardId, double amount, String billDetails) throws Exception;
}
