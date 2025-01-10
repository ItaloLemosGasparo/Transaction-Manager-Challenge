package com.vrsoftware.checkout.transaction_manager.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.vrsoftware.checkout.transaction_manager.dto.TransactionDTO;
import com.vrsoftware.checkout.transaction_manager.exceptions.ExceptionHandler;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.service.TransactionService;
import com.vrsoftware.checkout.transaction_manager.utils.ApiCaller;
import com.vrsoftware.checkout.transaction_manager.validator.CountryCurrencyValidator;
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
        try {
            switch (exchange.getRequestMethod()) {
                case "POST":
                    handlePostRequest(exchange);
                    break;
                case "GET":
                    handleGetRequest(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, "Unauthorised");
                    break;
            }
        } catch (JsonMappingException e) {
            ExceptionHandler.handleException(exchange, new Exception("Error processing JSON: " + e.getMessage()));
        } catch (Exception e) {
            ExceptionHandler.handleException(exchange, e);
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String requestBody = getRequestBody(exchange);
        Transaction transaction = objectMapper.readValue(requestBody, Transaction.class);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        if (!violations.isEmpty()) {
            ExceptionHandler.handleValidationException(exchange,
                    violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
            return;
        }

        String response = "Transaction saved with the following ID: " + transactionService.save(transaction).getId();
        sendResponse(exchange, 201, response);
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException, InterruptedException {
        String path = exchange.getRequestURI().getPath();
        String countryCurrency = path.matches("/transactions/[A-Za-z]+(-[A-Za-z ]+)*") ? path.substring(14) : "";

        String response;
        int statusCode;

        if (countryCurrency.length() > 1) {
            if (!CountryCurrencyValidator.validate(exchange, countryCurrency))
                return;

            List<TransactionDTO> transactionDTOList = transactionService.findAllWithExchangeRate(countryCurrency);
            response = transactionDTOList.isEmpty() ? "" : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transactionDTOList);
            statusCode = transactionDTOList.isEmpty() ? 204 : 200;
        } else {
            List<Transaction> transactionList = transactionService.findAll();
            response = transactionList.isEmpty() ? "" : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transactionList);
            statusCode = transactionList.isEmpty() ? 204 : 200;
        }

        sendResponse(exchange, statusCode, response);
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        if (statusCode == 204)
            exchange.sendResponseHeaders(statusCode, -1);
        else {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
        }
    }

    private static String getRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            return requestBody.toString();
        }
    }
}
