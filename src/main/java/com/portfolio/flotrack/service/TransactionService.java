package com.portfolio.flotrack.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.portfolio.flotrack.dto.request.TransactionRequest;
import com.portfolio.flotrack.dto.response.TransactionResponse;
import com.portfolio.flotrack.model.ExpenseCategory;
import com.portfolio.flotrack.model.IncomeCategory;
import com.portfolio.flotrack.model.Transaction;
import com.portfolio.flotrack.model.User;
import com.portfolio.flotrack.repository.ExpenseCategoryRepository;
import com.portfolio.flotrack.repository.IncomeCategoryRepository;
import com.portfolio.flotrack.repository.TransactionRepository;
import com.portfolio.flotrack.util.exceptions.ResourceNotFoundException;
import com.portfolio.flotrack.util.exceptions.UnauthorizedAccessException;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ExpenseCategoryRepository expenseCategoryRepository;

	@Autowired
	private IncomeCategoryRepository incomeCategoryRepository;

	// START: Filter methods for transactions

	public List<TransactionResponse> getTransactionsByIncomeCategory(Long incomeCategoryId, Long currentUserId) {
		return transactionRepository.findByIncomeCategoryIdAndUserId(incomeCategoryId, currentUserId)
				.stream()
				.map(this::convertTransactionToResponse)
				.toList();
	}

	public List<TransactionResponse> getTransactionsByExpenseCategory(Long expenseCategoryId, Long currentUserId) {
		return transactionRepository.findByExpenseCategoryIdAndUserId(expenseCategoryId, currentUserId)
				.stream()
				.map(this::convertTransactionToResponse)
				.toList();
	}

	public List<TransactionResponse> getTransactionsByDate(LocalDate date, Long currentUserId) {
		return transactionRepository.findByDateAndUserId(date, currentUserId)
				.stream()
				.map(this::convertTransactionToResponse)
				.toList();
	}

	public List<TransactionResponse> getTransactionsBetweenDates(LocalDate startDate, LocalDate endDate, Long currentUserId) {
		return transactionRepository.findByDateBetweenAndUserId(startDate, endDate, currentUserId)
				.stream()
				.map(this::convertTransactionToResponse)
				.toList();
	}

	public List<TransactionResponse> getTransactionsByIsExpense(Boolean isExpense, Long currentUserId) {
		return transactionRepository.findByIsExpenseAndUserId(isExpense, currentUserId)
				.stream()
				.map(this::convertTransactionToResponse)
				.toList();
	}

	public Map<String, Double> getTransactionsByIsExpenseAndSumAmountByCategory(Boolean isExpense, Long currentUserId) {
		List<Transaction> transactions = transactionRepository.findByIsExpenseAndUserId(isExpense, currentUserId);
		Map<String, Double> amountByCategory = new HashMap<>();
		for (Transaction transaction : transactions) {
			String categoryName = transaction.getIsExpense() ? transaction.getExpenseCategory().getName() : transaction.getIncomeCategory().getName();
			amountByCategory.put(categoryName, amountByCategory.getOrDefault(categoryName, 0.0) + transaction.getAmount());
		}
		return amountByCategory;
	}

	// END: Filter methods for transactions

	// START: CRUD methods for transactions

	public Page<TransactionResponse> getTransactionsForCurrentUser(Long currentUserId, int page, int size) {
		Pageable pageRequest = createPageRequestUsing(page, size);

		List<Transaction> transactions = transactionRepository.findByUserId(currentUserId);
		int totalTransactions = transactions.size();
		int start = (int) pageRequest.getOffset();
		int end = Math.min((start + pageRequest.getPageSize()), totalTransactions);

		List<Transaction> pageContent = transactions.subList(start, end);

		List<TransactionResponse> transactionResponses = pageContent.stream()
				.map(this::convertTransactionToResponse)
				.toList();
		return new PageImpl<>(transactionResponses, pageRequest, totalTransactions);
	}

	private Pageable createPageRequestUsing(int page, int size) {
    return PageRequest.of(page, size);
}

	public TransactionResponse getTransactionById(Long id, Long currentUserId) {
		Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUserId);
		if (transaction == null) {
			throw new ResourceNotFoundException("Transaction not found for the current user");
		}
		return convertTransactionToResponse(transaction);
	}

	@Transactional
	public TransactionResponse saveTransaction(TransactionRequest transactionRequest, User currentUser) {
		Transaction transaction = convertRequestToTransaction(transactionRequest, currentUser);
		Transaction savedTransaction = transactionRepository.save(transaction);
		return convertTransactionToResponse(savedTransaction);
	}

	@Transactional
	public TransactionResponse updateTransaction(Long id, TransactionRequest transactionRequest, Long currentUserId) {
		Transaction existingTransaction = transactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
		if (!existingTransaction.getUser().getId().equals(currentUserId)) {
			throw new UnauthorizedAccessException("You can only update your own transactions");
		}
		setTransactionFields(existingTransaction, transactionRequest, existingTransaction.getUser());
		Transaction updatedTransaction = transactionRepository.save(existingTransaction);
		return convertTransactionToResponse(updatedTransaction);
	}

	@Transactional
	public void deleteTransaction(Long id, Long currentUserId) {
		Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
		if (!transaction.getUser().getId().equals(currentUserId)) {
			throw new UnauthorizedAccessException("You can only delete your own transactions");
		}		
		transactionRepository.delete(transaction);
	}

	// END: CRUD methods for transactions

	// START: Summary methods

		public Map<String, Double> getMonthlySummaryForCurrentUser(LocalDate month, Long currentUserId) {
		Map <String, Double> monthlySummary = new HashMap<>();
		Double totalBalance = getBalanceForMonth(month, currentUserId);
		Double totalExpense = getTotalExpenseForMonth(month, currentUserId);
		Double totalIncome = getTotalIncomeForMonth(month, currentUserId);
		monthlySummary.put("totalBalance", totalBalance);
		monthlySummary.put("totalExpense", totalExpense);
		monthlySummary.put("totalIncome", totalIncome);
		return monthlySummary;
	}

	public Map<String, Double> getYearlySummaryForCurrentUser(LocalDate year, Long currentUserId) {
		Map<String, Double> yearlySummary = new HashMap<>();
		Double totalBalance = 0.0;
		Double totalExpense = 0.0;
		Double totalIncome = 0.0;

		for (int month = 1; month <= 12; month++) {
			LocalDate currentMonth = year.withMonth(month);
			totalBalance += getBalanceForMonth(currentMonth, currentUserId);
			totalExpense += getTotalExpenseForMonth(currentMonth, currentUserId);
			totalIncome += getTotalIncomeForMonth(currentMonth, currentUserId);
		}

		yearlySummary.put("totalBalance", totalBalance);
		yearlySummary.put("totalExpense", totalExpense);
		yearlySummary.put("totalIncome", totalIncome);
		return yearlySummary;
	}

	public Double getBalanceForMonth(LocalDate month, Long currentUserId) {
		LocalDate startDate = month.withDayOfMonth(1);
		LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
		return transactionRepository.findByDateBetweenAndUserId(startDate, endDate, currentUserId)
				.stream()
				.mapToDouble(Transaction::getAmount)
				.sum();
	}

	public Double getTotalExpenseForMonth(LocalDate month, Long currentUserId) {
		LocalDate startDate = month.withDayOfMonth(1);
		LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
		return transactionRepository.findByIsExpenseAndUserIdAndDateBetween(true, currentUserId, startDate, endDate)
				.stream()
				.mapToDouble(Transaction::getAmount)
				.sum();
	}

	public Double getTotalIncomeForMonth(LocalDate month, Long currentUserId) {
		LocalDate startDate = month.withDayOfMonth(1);
		LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
		return transactionRepository.findByIsExpenseAndUserIdAndDateBetween(false, currentUserId, startDate, endDate)
				.stream()
				.mapToDouble(Transaction::getAmount)
				.sum();
	}

	// END: Summary methods

	// START: Helper methods

	private Transaction convertRequestToTransaction(TransactionRequest transactionRequest, User currentUser) {
		Transaction transaction = new Transaction();
		transaction.setUser(currentUser);
		setTransactionFields(transaction, transactionRequest, transaction.getUser());
		return transaction;
	}

	private TransactionResponse convertTransactionToResponse(Transaction transaction) {
		TransactionResponse response = new TransactionResponse();
		response.setId(transaction.getId());
		response.setAmount(transaction.getAmount());
		response.setDescription(transaction.getDescription());
		response.setDate(transaction.getDate());
		response.setIsExpense(transaction.getIsExpense());
		if (transaction.getIsExpense()) {
			response.setCategoryName(transaction.getExpenseCategory().getName());
		} else {
			response.setCategoryName(transaction.getIncomeCategory().getName());
		}
		return response;
	}

	private Transaction setTransactionFields(Transaction transaction, TransactionRequest transactionRequest, User currentUser) {
		transaction.setAmount(transactionRequest.getAmount());
		transaction.setDescription(transactionRequest.getDescription());
		transaction.setDate(transactionRequest.getDate() != null ? transactionRequest.getDate() : LocalDate.now());
		transaction.setIsExpense(transactionRequest.getIsExpense());
		return validateCategoryOwnershipAndAssignOrThrow(transaction, transactionRequest, currentUser);
	}

	private Transaction validateCategoryOwnershipAndAssignOrThrow(Transaction transaction, TransactionRequest transactionRequest, User currentUser) {
		if (transaction.getIsExpense()) {
			ExpenseCategory expenseCategory = expenseCategoryRepository.findById(transactionRequest.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Expense category not found"));
			if(expenseCategory.getUser() != null && !expenseCategory.getUser().getId().equals(currentUser.getId())) {
				throw new UnauthorizedAccessException("Expense category does not belong to the current user");
			}
			transaction.setExpenseCategory(expenseCategory);
			transaction.setIncomeCategory(null);
		}
		else {
			IncomeCategory incomeCategory = incomeCategoryRepository.findById(transactionRequest.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Income category not found"));
			if(incomeCategory.getUser() != null && !incomeCategory.getUser().getId().equals(currentUser.getId())) {
				throw new UnauthorizedAccessException("Income category does not belong to the current user");
			}
			transaction.setIncomeCategory(incomeCategory);
			transaction.setExpenseCategory(null);

		}
		return transaction;
	}

	// END: Helper methods
}
