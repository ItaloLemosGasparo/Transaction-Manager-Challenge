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
        String path = exchange.getRequestURI().getPath();
        String response;
        int statusCode;

        try {
            switch (exchange.getRequestMethod()) {
                case "POST":
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
                    statusCode = 201;
                    break;

                case "GET":
                    String countryCurrency = path.matches("/transactions/[A-Za-z]+(-[A-Za-z ]+)*") ? path.substring(14) : "";

                    if (countryCurrency.length() > 1) {
                        if (!validateCountryCurrency(exchange, countryCurrency))
                            return;

                        List<TransactionDTO> transactionDTOList = transactionService.findAllWithExchangeRate(countryCurrency);
                        response = transactionDTOList.isEmpty() ? "" : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transactionDTOList);
                        statusCode = transactionDTOList.isEmpty() ? 204 : 200;
                    } else {
                        List<Transaction> transactionList = transactionService.findAll();
                        response = transactionList.isEmpty() ? "" : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transactionList);
                        statusCode = transactionList.isEmpty() ? 204 : 200;
                    }
                    break;

                default:
                    response = "Unauthorised";
                    statusCode = 405;
                    break;
            }

            sendResponse(exchange, statusCode, response);

        } catch (JsonMappingException e) {
            ExceptionHandler.handleException(exchange, new Exception("Error processing JSON: " + e.getMessage()));
        } catch (Exception e) {
            ExceptionHandler.handleException(exchange, e);
        }
    }

    private static boolean validateCountryCurrency(HttpExchange exchange, String countryCurrency) throws IOException, InterruptedException {
        List<String> countryCurrencies = ApiCaller.getCountryCurrencies();
        if (countryCurrencies.contains(countryCurrency))
            return true;
        else if (countryCurrencies.size() == 1)
            ExceptionHandler.handleValidationException(exchange, countryCurrencies.size() == 1 ? countryCurrencies.get(0) : "Invalid country currency");
        return false;
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        if (statusCode == 204)
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
