package com.portfolio.flotrack.dto.response;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TransactionResponse {
	private Long id;
    private String description;
    private Double amount;
    private Boolean isExpense;
    private String categoryName;
    private LocalDate date;
}
