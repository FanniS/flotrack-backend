package com.portfolio.flotrack.dto.request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TransactionRequest {
    private String description;
    private Double amount;
    private Boolean isExpense;
    private Long categoryId; // dynamic: can be income or expense depending on isExpense
    private LocalDate date;
}

