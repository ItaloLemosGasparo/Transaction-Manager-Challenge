package com.vrsoftware.checkout.transaction_manager.service;

import com.vrsoftware.checkout.transaction_manager.dto.TransactionDTO;
import com.vrsoftware.checkout.transaction_manager.mapper.TransactionMapper;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.repository.TransactionRepository;
import com.vrsoftware.checkout.transaction_manager.utils.ApiCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public List<TransactionDTO> findAllWithExchangeRate(String countryCurrency) {
        return transactionRepository.findAll().stream()
                .map(transaction -> TransactionMapper.mapTransactionToDto(countryCurrency, transaction)).collect(Collectors.toList());
    }
}
