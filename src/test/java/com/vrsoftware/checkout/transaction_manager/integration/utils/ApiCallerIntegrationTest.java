package com.vrsoftware.checkout.transaction_manager.integration.utils;

import com.vrsoftware.checkout.transaction_manager.utils.ApiCaller;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApiCallerIntegrationTest {

    @Test
    void testCheckExchangeRateWith() throws IOException, InterruptedException {
        LocalDateTime testDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        String exchangeRate = ApiCaller.checkExchangeRate("Brazil-Real", testDate);

        assertNotNull(exchangeRate);
        assertTrue(exchangeRate.matches("\\d+\\.\\d+"));
    }

    @Test
    void testGetCountryCurrenciesWith() throws IOException, InterruptedException {
        List<String> currencies = ApiCaller.getCountryCurrencies();

        assertNotNull(currencies);
        assertFalse(currencies.isEmpty(), "Currencies list should not be empty.");
        assertTrue(currencies.contains("Brazil-Real"));
    }
}
