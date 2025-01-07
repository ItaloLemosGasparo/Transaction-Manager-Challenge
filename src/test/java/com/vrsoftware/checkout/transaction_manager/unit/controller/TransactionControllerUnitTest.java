package com.vrsoftware.checkout.transaction_manager.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.vrsoftware.checkout.transaction_manager.controller.TransactionController;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.service.TransactionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerUnitTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Validator validator;

    @Mock
    private HttpExchange httpExchange;

    @Test
    @DisplayName("Should return 201")
    void handlePOSTRequestShouldReturn201() throws Exception {
        when(httpExchange.getRequestURI()).thenReturn(new URI("/transactions"));
        when(httpExchange.getRequestMethod()).thenReturn("POST");

        String requestBody = """
                {
                  "description": "Test Transaction",
                  "transactionDateTime": "2025-01-01T10:00:00",
                  "amount": 123.45
                }
                """;
        when(httpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestBody.getBytes()));

        Transaction recivedTransaction = new Transaction(
                null,
                "Test Transaction",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                new BigDecimal(123.45)
        );
        when(objectMapper.readValue(any(String.class), eq(Transaction.class))).thenReturn(recivedTransaction);

        when(validator.validate(any(Transaction.class))).thenReturn(Collections.emptySet());

        Transaction savedTransaction = new Transaction(
                UUID.randomUUID(),
                recivedTransaction.getDescription(),
                recivedTransaction.getTransactionDateTime(),
                recivedTransaction.getAmount()
        );
        when(transactionService.save(any(Transaction.class))).thenReturn(savedTransaction);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(httpExchange.getResponseBody()).thenReturn(outputStream);

        transactionController.handle(httpExchange);

        verify(httpExchange).sendResponseHeaders(eq(201), anyLong());
        assertEquals("Transaction saved with the following ID: " + savedTransaction.getId(), outputStream.toString());
    }

    @Test
    @DisplayName("Should return 400")
    void handlePOSTRequestShouldReturn400() throws Exception {
        when(httpExchange.getRequestURI()).thenReturn(new URI("/transactions"));
        when(httpExchange.getRequestMethod()).thenReturn("POST");

        String requestBody = """
                {
                  "description": "012346578901234657890123465789012346578901234657890123465789",
                  "transactionDateTime": "2050-01-01T10:00:00",
                  "amount": -123.45
                }
                """;
        when(httpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestBody.getBytes()));

        Transaction recivedTransaction = new Transaction(
                null,
                "012346578901234657890123465789012346578901234657890123465789",
                LocalDateTime.of(2050, 1, 1, 10, 0),
                new BigDecimal(-123.45)
        );
        when(objectMapper.readValue(any(String.class), eq(Transaction.class))).thenReturn(recivedTransaction);

        Set<ConstraintViolation<Transaction>> violations = getConstraintViolations();
        when(validator.validate(any(Transaction.class))).thenReturn(violations);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(httpExchange.getResponseBody()).thenReturn(outputStream);

        transactionController.handle(httpExchange);

        verify(httpExchange).sendResponseHeaders(eq(400), anyLong());
        assert outputStream.toString().contains("Description can have only 50 characters.");
        assert outputStream.toString().contains("The Transaction date must be in the past or present.");
        assert outputStream.toString().contains("The price must be greater than 0.");
    }

    private static Set<ConstraintViolation<Transaction>> getConstraintViolations() {
        Set<ConstraintViolation<Transaction>> violations = new HashSet<>();
        ConstraintViolation<Transaction> violation1 = mock(ConstraintViolation.class);
        when(violation1.getMessage()).thenReturn("Description can have only 50 characters.");
        violations.add(violation1);
        ConstraintViolation<Transaction> violation2 = mock(ConstraintViolation.class);
        when(violation2.getMessage()).thenReturn("The Transaction date must be in the past or present.");
        violations.add(violation2);
        ConstraintViolation<Transaction> violation3 = mock(ConstraintViolation.class);
        when(violation3.getMessage()).thenReturn("The price must be greater than 0.");
        violations.add(violation3);
        return violations;
    }
}
