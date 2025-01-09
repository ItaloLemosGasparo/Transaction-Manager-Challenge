package com.vrsoftware.checkout.transaction_manager.unit.mapper;

import com.vrsoftware.checkout.transaction_manager.dto.TransactionDTO;
import com.vrsoftware.checkout.transaction_manager.mapper.TransactionMapper;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.utils.ApiCaller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionMapperUnitTest {

    @InjectMocks
    private TransactionMapper transactionMapper;

    @Test
    void mapTransactionToDtoTest() throws IOException, InterruptedException {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "Test Description",
                LocalDateTime.now(),
                new BigDecimal(123.45)
        );

        try (MockedStatic<ApiCaller> mockedStatic = mockStatic(ApiCaller.class)) {
            mockedStatic.when(() -> ApiCaller.checkExchangeRate("Brazil-Real", transaction.getTransactionDateTime())).thenReturn("4.13");

            TransactionDTO transactionDTO = TransactionMapper.mapTransactionToDto("Brazil-Real", transaction);

            assertNotNull(transactionDTO.getExchange_rate());
            assertNotNull(transactionDTO.getConverted_amount());
            assertNotNull(transactionDTO.getCurrency());
        }
    }
}
