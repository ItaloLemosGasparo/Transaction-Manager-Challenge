package com.vrsoftware.checkout.transaction_manager.unit.Validator;

import com.sun.net.httpserver.HttpExchange;
import com.vrsoftware.checkout.transaction_manager.utils.ApiCaller;
import com.vrsoftware.checkout.transaction_manager.validator.CountryCurrencyValidator;
import com.vrsoftware.checkout.transaction_manager.exceptions.ExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CountryCurrencyValidatorUnitTest {

    @Test
    @DisplayName("Should return true for valid country currency")
    public void validateOk() throws IOException, InterruptedException {
        try (MockedStatic<ApiCaller> mockedStatic = mockStatic(ApiCaller.class)) {
            mockedStatic.when(() -> ApiCaller.getCountryCurrencies()).thenReturn(Arrays.asList("AFGHANISTAN-AFGHANI", "ALBANIA-LEK", "ARGENTINA-PESO"));

            HttpExchange exchangeMock = mock(HttpExchange.class);
            OutputStream outputStreamMock = mock(OutputStream.class);
            when(exchangeMock.getResponseBody()).thenReturn(outputStreamMock);

            boolean result = CountryCurrencyValidator.validate(exchangeMock, "ALBANIA-LEK");

            assertTrue(result);
        }
    }

    @Test
    @DisplayName("Should return false for invalid country currency")
    public void validateNotOk() throws IOException, InterruptedException {
        try (MockedStatic<ApiCaller> mockedStatic = mockStatic(ApiCaller.class)) {
            mockedStatic.when(() -> ApiCaller.getCountryCurrencies()).thenReturn(Arrays.asList("AFGHANISTAN-AFGHANI", "ALBANIA-LEK", "ARGENTINA-PESO"));

            // Mock HttpExchange
            HttpExchange exchangeMock = mock(HttpExchange.class);
            OutputStream outputStreamMock = mock(OutputStream.class);
            when(exchangeMock.getResponseBody()).thenReturn(outputStreamMock);

            boolean result = CountryCurrencyValidator.validate(exchangeMock, "Brazil-Real");

            assertFalse(result);
            verify(exchangeMock, times(1)).sendResponseHeaders(400, "Validation Error: Invalid country currency".getBytes().length);
        }
    }
}
