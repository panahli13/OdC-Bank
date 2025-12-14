package com.example.banksystem.repository;

import com.example.banksystem.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.fromAccountId = :accountId " +
            "AND t.type = 'WITHDRAW' " +
            "AND FUNCTION('DATE', t.createdAt) = :date")
    double getWithdrawnAmountToday(@Param("accountId") Long accountId,
                                   @Param("date") LocalDate date);


    List<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long accountId);
}
