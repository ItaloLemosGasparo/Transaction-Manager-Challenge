package com.vrsoftware.checkout.transaction_manager.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import java.util.*;

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
    @DisplayName("Should return 201 created")
    void handlePOSTRequest() throws Exception {
        when(httpExchange.getRequestMethod()).thenReturn("POST");

        String requestBody = """
                {
                  "description": "Test Transaction",
                  "transactionDateTime": "2025-01-01T10:00:00",
                  "amount": 123.45
                }
                """;
        when(httpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestBody.getBytes()));

        Transaction receivedTransaction = new Transaction(
                null,
                "Test Transaction",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                new BigDecimal(123.45)
        );
        when(objectMapper.readValue(any(String.class), eq(Transaction.class))).thenReturn(receivedTransaction);

        when(validator.validate(any(Transaction.class))).thenReturn(Collections.emptySet());

        Transaction savedTransaction = new Transaction(
                UUID.randomUUID(),
                receivedTransaction.getDescription(),
                receivedTransaction.getTransactionDateTime(),
                receivedTransaction.getAmount()
        );
        when(transactionService.save(any(Transaction.class))).thenReturn(savedTransaction);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(httpExchange.getResponseBody()).thenReturn(outputStream);

        transactionController.handle(httpExchange);

        verify(httpExchange).sendResponseHeaders(eq(201), anyLong());
        assertEquals("Transaction saved with the following ID: " + savedTransaction.getId(), outputStream.toString());
    }

    @Test
    @DisplayName("Should return 200 and 2 transactions")
    void handleGETRequest() throws Exception {
        when(httpExchange.getRequestURI()).thenReturn(new URI("/transactions"));
        when(httpExchange.getRequestMethod()).thenReturn("GET");

        when(transactionService.findAll()).thenReturn(Arrays.asList(
                new Transaction(UUID.randomUUID(), "Transaction 1", LocalDateTime.now(), new BigDecimal("100.00")),
                new Transaction(UUID.randomUUID(), "Transaction 2", LocalDateTime.now(), new BigDecimal("200.00"))
        ));

        ObjectWriter objectWriterMock = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriterMock);
        when(objectWriterMock.writeValueAsString(any())).thenReturn("""
                [
                  {
                    "id": "1",
                    "description": "Transaction 1",
                    "date": "2025-01-08T10:00:00",
                    "amount": 100.00
                  },
                  {
                    "id": "2",
                    "description": "Transaction 2",
                    "date": "2025-01-08T10:00:00",
                    "amount": 200.00
                  }
                ]
                """);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(httpExchange.getResponseBody()).thenReturn(outputStream);

        transactionController.handle(httpExchange);

        verify(httpExchange).sendResponseHeaders(eq(200), anyLong());
        assertEquals("""
                [
                  {
                    "id": "1",
                    "description": "Transaction 1",
                    "date": "2025-01-08T10:00:00",
                    "amount": 100.00
                  },
                  {
                    "id": "2",
                    "description": "Transaction 2",
                    "date": "2025-01-08T10:00:00",
                    "amount": 200.00
                  }
                ]
                """, outputStream.toString());
    }
}
