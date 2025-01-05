package com.vrsoftware.checkout.transaction_manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

@Controller
public class TransactionController implements HttpHandler {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String requestBody = reader.readLine();

            try {
                Transaction transaction = objectMapper.readValue(requestBody, Transaction.class);

                Transaction savedTransaction = transactionService.save(transaction);

                response = "Transaction saved with the following ID: " + savedTransaction.getId();
                statusCode = 200;
            } catch (JsonMappingException e) {
                response = "Error mapping the JSON to Transaction object: " + e.getMessage();
                statusCode = 400;
            } catch (JsonProcessingException e) {
                response = "Error processing the JSON: " + e.getMessage();
                statusCode = 400;
            }
        } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            List<Transaction> transactionList = transactionService.findAll();
            response = transactionList.isEmpty() ? "" : objectMapper.writeValueAsString(transactionList);
            statusCode = transactionList.isEmpty() ? 204 : 200;
        } else {
            response = "Not authorised";
            statusCode = 405;
        }

        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }
}
