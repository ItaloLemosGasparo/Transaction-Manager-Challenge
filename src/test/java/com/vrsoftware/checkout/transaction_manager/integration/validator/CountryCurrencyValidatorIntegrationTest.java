package com.vrsoftware.checkout.transaction_manager.integration.validator;

import com.sun.net.httpserver.HttpExchange;
import com.vrsoftware.checkout.transaction_manager.validator.CountryCurrencyValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CountryCurrencyValidatorIntegrationTest {

    @Test
    void validateOK() throws IOException, InterruptedException {
        HttpExchange mockExchange = Mockito.mock(HttpExchange.class);
        CountryCurrencyValidator countryCurrencyValidator = new CountryCurrencyValidator();
        boolean result = countryCurrencyValidator.validate(mockExchange, "Brazil-Real");

        assertTrue(result);
    }

    @Test
    void validateNotOK() throws IOException, InterruptedException {
        HttpExchange mockExchange = Mockito.mock(HttpExchange.class);
        Mockito.when(mockExchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        CountryCurrencyValidator countryCurrencyValidator = new CountryCurrencyValidator();

        boolean result = countryCurrencyValidator.validate(mockExchange, "Bral-Rel");

        assertFalse(result);
    }
}
