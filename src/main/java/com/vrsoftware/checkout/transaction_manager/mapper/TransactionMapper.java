package com.vrsoftware.checkout.transaction_manager.mapper;

import com.vrsoftware.checkout.transaction_manager.dto.TransactionDTO;
import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.utils.ApiCaller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionMapper {

    public static TransactionDTO mapTransactionToDto(String countryCurrency, Transaction transaction) {
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
