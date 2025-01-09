package com.vrsoftware.checkout.transaction_manager.integration.model;

import com.vrsoftware.checkout.transaction_manager.controller.TransactionController;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class TransactionIntegrationTest {

    @Autowired
    private TransactionController transactionController;

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should have no violations")
    void noViolations() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "Test Description",
                LocalDateTime.now(),
                new BigDecimal(123.456)
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assert violations.isEmpty();
    }

    @Test
    @DisplayName("Should have description size violation")
    void descriptionViolation() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "012345678901234567890123456789012345678901234567890", //51 characters
                LocalDateTime.now(),
                new BigDecimal(123.456)
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assert !violations.isEmpty();
        assert violations.stream().anyMatch(v -> v.getMessage().equals("Description can have only 50 characters."));
    }

    @Test
    @DisplayName("Should have transaction date time in the future violation")
    void transactionDateTimeViolation() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "Test Description",
                LocalDateTime.of(3000, 1, 1, 10, 0),
                new BigDecimal(123.456)
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assert !violations.isEmpty();
        assert violations.stream().anyMatch(v -> v.getMessage().equals("The Transaction date must be in the past or present."));
    }

    @Test
    @DisplayName("Should have negative amount violation")
    void amountViolation() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "Test Description",
                LocalDateTime.now(),
                new BigDecimal(-123.456)
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assert !violations.isEmpty();
        assert violations.stream().anyMatch(v -> v.getMessage().equals("The price must be greater than 0."));
    }
}
