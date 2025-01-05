package com.vrsoftware.checkout.transaction_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private UUID id;
    private String description;
    private LocalDateTime transactionDateTime;
    private BigDecimal amount;
    private String exchange_rate;
    private BigDecimal converted_amount;
    private String currency;
}
