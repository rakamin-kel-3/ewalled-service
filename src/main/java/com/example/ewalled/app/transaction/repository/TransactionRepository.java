package com.example.ewalled.app.transaction.repository;

import com.example.ewalled.entity.Transaction;
import com.example.ewalled.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<List<Transaction>> findBySenderAccountIdOrReceipentAccountIdOrderByCreatedAtDesc(int senderAccountId, int receipentAccountId);
}
