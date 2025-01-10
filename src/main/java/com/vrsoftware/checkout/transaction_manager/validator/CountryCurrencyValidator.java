package com.vrsoftware.checkout.transaction_manager.validator;

import com.sun.net.httpserver.HttpExchange;
import com.vrsoftware.checkout.transaction_manager.exceptions.ExceptionHandler;
import com.vrsoftware.checkout.transaction_manager.utils.ApiCaller;

import java.io.IOException;
import java.util.List;

public class CountryCurrencyValidator {

    public static boolean validate(HttpExchange exchange, String countryCurrency) throws IOException, InterruptedException {
        List<String> countryCurrencies = ApiCaller.getCountryCurrencies();
        if (countryCurrencies.contains(countryCurrency)) {
            return true;
        } else {
            String errorMessage = countryCurrencies.size() == 1 ? countryCurrencies.get(0) : "Invalid country currency";
            ExceptionHandler.handleValidationException(exchange, errorMessage);
            return false;
        }
    }
}