package com.vrsoftware.checkout.transaction_manager.service;

import com.vrsoftware.checkout.transaction_manager.dto.TransactionDTO;
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
                .map(transaction -> mapTransactionToDto(countryCurrency, transaction)).collect(Collectors.toList());
    }

    private static TransactionDTO mapTransactionToDto(String countryCurrency, Transaction transaction) {
        try {
            TransactionDTO transactionDTO = new TransactionDTO(
                    transaction.getId(),
                    transaction.getDescription(),
                    transaction.getTransactionDateTime(),
                    transaction.getAmount()
            );

            String apiResponse = ApiCaller.checkExchangeRate(countryCurrency, transaction.getTransactionDateTime());
            BigDecimal bigDecimal = tryParseBigDecimal(apiResponse);

            if (bigDecimal != null) {
                transactionDTO.setExchange_rate(bigDecimal.setScale(2, RoundingMode.HALF_UP).toString());
                transactionDTO.setConverted_amount(transaction.getAmount().multiply(bigDecimal).setScale(2, RoundingMode.HALF_UP));
                transactionDTO.setCurrency(countryCurrency);
            } else
                transactionDTO.setExchange_rate(apiResponse);

            return transactionDTO;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static BigDecimal tryParseBigDecimal(String str) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
