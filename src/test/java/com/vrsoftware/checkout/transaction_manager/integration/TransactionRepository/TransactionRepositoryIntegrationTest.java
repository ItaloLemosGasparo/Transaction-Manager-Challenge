package com.vrsoftware.checkout.transaction_manager.integration.TransactionRepository;

import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TransactionRepositoryIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveTheTransactionAndGenerateTheUUID() {
        Transaction transaction = new Transaction(
                null,
                "Test Description",
                LocalDateTime.now(),
                new BigDecimal(132.12) //Already rounded
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        assertNotNull(savedTransaction.getId());
        assertEquals(transaction.getDescription(), savedTransaction.getDescription());
        assertEquals(transaction.getTransactionDateTime(), savedTransaction.getTransactionDateTime());
        assertEquals(transaction.getAmount(), savedTransaction.getAmount());
    }

    @Test
    void shouldSaveTheTransactionAndRoundTheAmount() {
        Transaction transaction = new Transaction(
                null,
                "Test Description",
                LocalDateTime.now(),
                new BigDecimal(123.128) //Will be round to 132.13
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        assertNotNull(savedTransaction.getId());
        assertEquals(transaction.getDescription(), savedTransaction.getDescription());
        assertEquals(transaction.getTransactionDateTime(), savedTransaction.getTransactionDateTime());
        assertEquals("123.13", savedTransaction.getAmount().toString());
    }

    @Test
    @Transactional
    void shouldUpdateTheTransactionAndRoundTheAmount() {
        Transaction transaction = new Transaction(
                null,
                "Test Description",
                LocalDateTime.now(),
                new BigDecimal("123.128") //Will be round to 123.13
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        savedTransaction.setAmount(new BigDecimal("543.321"));

        entityManager.merge(savedTransaction);
        entityManager.flush();

        Transaction updatedTransaction = transactionRepository.save(savedTransaction);

        assertNotNull(updatedTransaction.getId());
        assertEquals(savedTransaction.getDescription(), updatedTransaction.getDescription());
        assertEquals(savedTransaction.getTransactionDateTime(), updatedTransaction.getTransactionDateTime());
        assertEquals("543.32", updatedTransaction.getAmount().toString());
    }

}
