package com.vrsoftware.checkout.transaction_manager.exceptions;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ExceptionHandler {

    public static void handleException(HttpExchange exchange, Exception exception) {
        try {
            String response = "Internal server Error: " + exception.getMessage();
            exchange.sendResponseHeaders(500, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleValidationException(HttpExchange exchange, String validationMessage) {
        try {
            String response = "Validation Error: " + validationMessage;
            exchange.sendResponseHeaders(400, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
