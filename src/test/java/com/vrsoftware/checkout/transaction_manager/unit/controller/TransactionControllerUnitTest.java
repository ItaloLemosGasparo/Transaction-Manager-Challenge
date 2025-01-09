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
    @DisplayName("Should return 200 and 2 transactions")
    void handleGETRequest() throws Exception {
        when(httpExchange.getRequestURI()).thenReturn(new URI("/transactions"));
        when(httpExchange.getRequestMethod()).thenReturn("GET");

        when(transactionService.findAll()).thenReturn(new ArrayList<>(Arrays.asList(
                new Transaction(UUID.fromString("4e496802-d86d-4098-aa66-5c7e7ae0710e"), "Test Transaction", LocalDateTime.now(), new BigDecimal(123.45)),
                new Transaction(UUID.fromString("746021fb-8f34-4407-8ffb-603d0abacd19"), "Test Transaction", LocalDateTime.now(), new BigDecimal(123.45))
        )));

        ObjectWriter objectWriterMock = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriterMock);
        when(objectWriterMock.writeValueAsString(any(ArrayList.class))).thenReturn("""
                [
                  {
                    "id": "4e496802-d86d-4098-aa66-5c7e7ae0710e",
                    "description": "Test Transaction",
                    "date": "2025-01-08T10:00:00",
                    "amount": 123.45
                  },
                  {
                    "id": "746021fb-8f34-4407-8ffb-603d0abacd19",
                    "description": "Test Transaction",
                    "date": "2025-01-08T10:00:00",
                    "amount": 123.45
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
                    "id": "4e496802-d86d-4098-aa66-5c7e7ae0710e",
                    "description": "Test Transaction",
                    "date": "2025-01-08T10:00:00",
                    "amount": 123.45
                  },
                  {
                    "id": "746021fb-8f34-4407-8ffb-603d0abacd19",
                    "description": "Test Transaction",
                    "date": "2025-01-08T10:00:00",
                    "amount": 123.45
                  }
                ]
                """, outputStream.toString());
    }
}
