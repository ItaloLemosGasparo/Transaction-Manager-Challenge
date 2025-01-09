package com.vrsoftware.checkout.transaction_manager.unit.utils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import com.vrsoftware.checkout.transaction_manager.utils.ApiCaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApiCallerUnitTest {

    private HttpClient mockHttpClient;
    private HttpResponse<String> mockHttpResponse;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockHttpResponse = mock(HttpResponse.class);
        ApiCaller.setHttpClient(mockHttpClient);
    }

    @Test
    @DisplayName("Should build the right url then return the exchange rate")
    void checkExchangeRate() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        String jsonResponse = """
                {
                    "data": [
                        {"country_currency_desc": "Brazil-Real", "exchange_rate": "5.434", "record_date": "2024-09-30"}
                    ]
                }
                """;
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpResponse.statusCode()).thenReturn(200);

        LocalDateTime testDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        String exchangeRate = ApiCaller.checkExchangeRate("Brazil-Real", testDate);

        assertEquals("5.434", exchangeRate, "Different exchange Rate");

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient, times(1)).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));

        String expectedUrl = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange"
                + "?fields=country_currency_desc,exchange_rate,record_date&filter=country_currency_desc:eq:Brazil-Real,record_date:gte:2024-07-01,record_date:lte:2025-01-01&sort=-record_date";
        assertEquals(expectedUrl, requestCaptor.getValue().uri().toString());
    }

    @Test
    @DisplayName("Should build the right url then return the list of country currencies")
    void getCountryCurrencies() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        String jsonResponse = """
                {
                    "data": [
                        {"country_currency_desc": "AFGHANISTAN-AFGHANI"},
                        {"country_currency_desc": "ALBANIA-LEK"},
                        {"country_currency_desc": "ALGERIA-DINAR"}
                    ]
                }
                """;
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpResponse.statusCode()).thenReturn(200);

        List<String> currencies = ApiCaller.getCountryCurrencies();

        assertEquals(3, currencies.size());
        assertEquals("AFGHANISTAN-AFGHANI", currencies.get(0));
        assertEquals("ALBANIA-LEK", currencies.get(1));
        assertEquals("ALGERIA-DINAR", currencies.get(2));

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));

        String expectedUrl = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=country_currency_desc";
        assertEquals(expectedUrl, requestCaptor.getValue().uri().toString());
    }
}
