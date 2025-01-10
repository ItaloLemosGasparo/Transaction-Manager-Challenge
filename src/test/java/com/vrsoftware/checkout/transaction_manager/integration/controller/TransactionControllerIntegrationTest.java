package com.vrsoftware.checkout.transaction_manager.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TransactionControllerIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/transactions";

    private final ObjectMapper objectMapper = new ObjectMapper();

//    @Test
//    @DisplayName("Should create a transaction and return 201")
//    void handlePOSTRequest() throws Exception {
//        objectMapper.registerModule(new JavaTimeModule());
//
//        Transaction transaction = new Transaction(
//                null,
//                "Test Transaction",
//                LocalDateTime.of(2025, 1, 1, 10, 0),
//                new BigDecimal(123.45)
//        );
//
//        String requestBody = objectMapper.writeValueAsString(transaction);
//
//        Thread.sleep(2000);
//        ResponseEntity<String> response = restTemplate.postForEntity(new URI(BASE_URL), requestBody, String.class);
//
//        assertEquals(201, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//        assertEquals(true, response.getBody().contains("Transaction saved with the following ID:"));
//    }
//
//    @Test
//    @DisplayName("Should retrieve all transactions and return 200")
//    void handleGETRequest() throws Exception {
//        Thread.sleep(2000);
//        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL, String.class);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//    }
}
