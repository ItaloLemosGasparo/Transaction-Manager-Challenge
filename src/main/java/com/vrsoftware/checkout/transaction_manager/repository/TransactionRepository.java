package com.vrsoftware.checkout.transaction_manager.repository;

import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
