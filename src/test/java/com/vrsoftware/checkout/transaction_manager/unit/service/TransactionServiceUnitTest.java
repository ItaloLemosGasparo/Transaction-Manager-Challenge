package com.vrsoftware.checkout.transaction_manager.unit.service;

import com.vrsoftware.checkout.transaction_manager.repository.TransactionRepository;
import com.vrsoftware.checkout.transaction_manager.service.TransactionService;
import com.vrsoftware.checkout.transaction_manager.utils.ApiCaller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceUnitTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ApiCaller apiCaller;

    @Test
    void shouldSave() {

    }
}
