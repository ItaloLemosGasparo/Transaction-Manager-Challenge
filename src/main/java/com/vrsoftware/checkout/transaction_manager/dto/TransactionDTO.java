package com.vrsoftware.checkout.transaction_manager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class TransactionDTO {

    private UUID id;
    private String description;
    private LocalDateTime transactionDateTime;
    private BigDecimal amount;
    private String exchange_rate;
    private BigDecimal converted_amount;
    private String currency;

    public TransactionDTO(UUID id, String description, LocalDateTime transactionDateTime, BigDecimal amount) {
        this.id = id;
        this.description = description;
        this.transactionDateTime = transactionDateTime;
        this.amount = amount;
    }
}
