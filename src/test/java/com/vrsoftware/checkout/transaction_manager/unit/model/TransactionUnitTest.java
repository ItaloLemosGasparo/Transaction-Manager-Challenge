package com.vrsoftware.checkout.transaction_manager.unit.model;

import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TransactionUnitTest {

    @Test
    void roundingTest() {
        Transaction transaction = new Transaction(
                null,
                "Test Description",
                LocalDateTime.now(),
                new BigDecimal("123.128")
        );

        transaction.roundAmount();

        assertEquals("123.13", transaction.getAmount().toString());
    }
}
