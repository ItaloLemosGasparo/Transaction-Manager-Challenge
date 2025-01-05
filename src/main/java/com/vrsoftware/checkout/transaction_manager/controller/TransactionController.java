package com.vrsoftware.checkout.transaction_manager.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.vrsoftware.checkout.transaction_manager.exceptions.ExceptionHandler;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.service.TransactionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class TransactionController implements HttpHandler {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        try {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String RequestBody = getRequestBody(exchange);

                Transaction transaction = objectMapper.readValue(RequestBody, Transaction.class);

                Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction); //@Valid
                if (!violations.isEmpty()) {
                    ExceptionHandler.handleValidationException(exchange,
                            violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "))
                    );
                    return;
                }

                response = "Transaction saved with the following ID: " + transactionService.save(transaction).getId();
                statusCode = 200;

            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Transaction> transactionList = transactionService.findAll();
                response = transactionList.isEmpty() ? "No transactions found" : objectMapper.writeValueAsString(transactionList);
                statusCode = transactionList.isEmpty() ? 204 : 200;

            } else {
                response = "Unauthorised";
                statusCode = 405;
            }

            sendResponse(exchange, statusCode, response);

        } catch (JsonMappingException e) {
            ExceptionHandler.handleException(exchange, new Exception("Error processing JSON: " + e.getMessage()));
        } catch (Exception e) {
            ExceptionHandler.handleException(exchange, e);
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        if (statusCode == 204) //No Content
            exchange.sendResponseHeaders(statusCode, -1);
        else {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
        }
        exchange.getResponseBody().close();
    }

    private static String getRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        return requestBody.toString();
    }

}
