package com.portfolio.flotrack.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.flotrack.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findByUserId(Long userId);
	Transaction findByIdAndUserId(Long id, Long userId);
	List<Transaction> findByIncomeCategoryIdAndUserId(Long incomeCategoryId, Long userId);
	List<Transaction> findByExpenseCategoryIdAndUserId(Long expenseCategoryId, Long userId);
	List<Transaction> findByDateAndUserId(LocalDate date, Long userId);
	List<Transaction> findByDateBetweenAndUserId(LocalDate startDate, LocalDate endDate, Long userId);
	List<Transaction> findByIsExpenseAndUserId(Boolean isExpense, Long userId);
	List<Transaction> findByIsExpenseAndUserIdAndDateBetween(Boolean isExpense, Long userId, LocalDate startDate, LocalDate endDate);
}
