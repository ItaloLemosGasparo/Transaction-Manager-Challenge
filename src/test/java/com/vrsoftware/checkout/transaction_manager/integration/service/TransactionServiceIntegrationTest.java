package com.vrsoftware.checkout.transaction_manager.integration.service;

import com.vrsoftware.checkout.transaction_manager.dto.TransactionDTO;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.repository.TransactionRepository;
import com.vrsoftware.checkout.transaction_manager.service.TransactionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        transactionRepository.save(new Transaction(null, "Test Description", LocalDateTime.now(), new BigDecimal(123.45)));
        transactionRepository.save(new Transaction(null, "Test Description", LocalDateTime.now(), new BigDecimal(123.45)));
        transactionRepository.save(new Transaction(null, "Test Description", LocalDateTime.now(), new BigDecimal(123.45)));
    }

    @Test
    @DisplayName("Should save a new transaction")
    void saveTransactionTest() {
        Transaction transaction = new Transaction(null, "Test Description", LocalDateTime.now(), new BigDecimal(123.45));

        Transaction savedTransaction = transactionService.save(transaction);

        assertNotNull(savedTransaction.getId());
        assertEquals(transaction.getDescription(), savedTransaction.getDescription());
        assertEquals(transaction.getTransactionDateTime(), savedTransaction.getTransactionDateTime());
        assertEquals(transaction.getAmount(), savedTransaction.getAmount());
    }

    @Test
    @DisplayName("Should find all 3 transactions")
    void findAllTest() {
        List<Transaction> transactionList = transactionService.findAll();

        assertNotNull(transactionList);
        assertTrue(transactionList.size() > 2);
    }

    @Test
    @DisplayName("Should find all 3 transactions and search for the exchange rate")
    void findAllWithExchangeRateTest() {
        List<TransactionDTO> transactionDTOList = transactionService.findAllWithExchangeRate("Brazil-Real");

        assertNotNull(transactionDTOList);
        transactionDTOList.forEach(transactionDTO -> assertNotNull(transactionDTO.getExchange_rate()));
    }
}
