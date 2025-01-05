package com.vrsoftware.checkout.transaction_manager.service;

import com.vrsoftware.checkout.transaction_manager.model.Transaction;
import com.vrsoftware.checkout.transaction_manager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .map(transaction -> {
                    try {
                        TransactionDTO transactionDTO = new TransactionDTO();
                        transactionDTO.setId(transaction.getId());
                        transactionDTO.setDescription(transaction.getDescription());
                        transactionDTO.setTransactionDateTime(transaction.getTransactionDateTime());
                        transactionDTO.setAmount(transaction.getAmount());

                        String apiResponse = ApiCaller.checkExchangeRate(countryCurrency, transaction.getTransactionDateTime());
                        BigDecimal bigDecimal = tryParseBigDecimal(apiResponse);

                        if (bigDecimal != null) {
                            transactionDTO.setExchange_rate(bigDecimal.toString());
                            transactionDTO.setConverted_amount(transaction.getAmount().multiply(bigDecimal).setScale(2, RoundingMode.HALF_UP));
                            transactionDTO.setCurrency(countryCurrency);
                        } else
                            transactionDTO.setExchange_rate(apiResponse);

                        return transactionDTO;
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    public static BigDecimal tryParseBigDecimal(String str) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
