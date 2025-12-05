package com.portfolio.flotrack.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.flotrack.dto.request.TransactionRequest;
import com.portfolio.flotrack.dto.response.TransactionResponse;
import com.portfolio.flotrack.helper.UserAuthHelper;
import com.portfolio.flotrack.service.TransactionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private UserAuthHelper userAuthHelper;


	// START: Endpoints for transaction management

	@GetMapping("/manage")
	public ResponseEntity<Page<TransactionResponse>> getTransactions(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<TransactionResponse> transactions = transactionService.getTransactionsForCurrentUser(userAuthHelper.getCurrentUserId(),
				page, size);
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Page-Number", String.valueOf(transactions.getNumber()));
		headers.add("X-Page-Size", String.valueOf(transactions.getSize()));
		return ResponseEntity.ok().headers(headers).body(transactions);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
		TransactionResponse transaction = transactionService.getTransactionById(id, userAuthHelper.getCurrentUserId());
		return ResponseEntity.ok(transaction);
	}

	@GetMapping("/summary/month")
	public ResponseEntity<Map<String, Double>> getMonthlySummaryForCurrentUser(@RequestParam LocalDate month) {
		Map<String, Double> summary = transactionService.getMonthlySummaryForCurrentUser(month, userAuthHelper.getCurrentUserId());
		return ResponseEntity.ok(summary);
	}

	@GetMapping("/summary/year")
	public ResponseEntity<Map<String, Double>> getYearlySummaryForCurrentUser(@RequestParam LocalDate year) {
		Map<String, Double> summary = transactionService.getYearlySummaryForCurrentUser(year, userAuthHelper.getCurrentUserId());
		return ResponseEntity.ok(summary);
	}

	@PostMapping("/create")
	public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest transactionRequest) {
		TransactionResponse transaction = transactionService.saveTransaction(transactionRequest, userAuthHelper.getCurrentUser());
		return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long id,
			@RequestBody TransactionRequest transactionRequest) {
		TransactionResponse transaction = transactionService.updateTransaction(id, transactionRequest,
				userAuthHelper.getCurrentUserId());
		return ResponseEntity.ok(transaction);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
		transactionService.deleteTransaction(id, userAuthHelper.getCurrentUserId());
		return ResponseEntity.noContent().build();
	}

	// END: Endpoints for transaction management

}
