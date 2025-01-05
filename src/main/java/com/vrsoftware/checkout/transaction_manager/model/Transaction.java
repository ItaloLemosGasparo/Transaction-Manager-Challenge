package com.vrsoftware.checkout.transaction_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Size(max = 50, message = "Description can have only 50 characters.")
    private String description;

    @NotNull(message = "The Transaction date can't be null.")
    @PastOrPresent(message = "The Transaction date must be in the past or present.")
    @Column(nullable = false)
    private LocalDateTime transactionDateTime;

    @NotNull(message = "The value can't be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "The price must be greater than 0.")
    @Column(nullable = false)
    private BigDecimal amount;

    @PreUpdate
    @PrePersist
    private void roundAmount() {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);  // Arredonda para o centavo mais próximo
    }
}