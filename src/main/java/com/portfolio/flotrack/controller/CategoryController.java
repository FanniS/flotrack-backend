package com.portfolio.flotrack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.flotrack.helper.UserAuthHelper;
import com.portfolio.flotrack.model.ExpenseCategory;
import com.portfolio.flotrack.model.IncomeCategory;
import com.portfolio.flotrack.service.CategoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserAuthHelper userAuthHelper;

	// START: Endpoints for categories

	@GetMapping("/income")
	public ResponseEntity<List<IncomeCategory>> getIncomeCategories() {
		List<IncomeCategory> incomeCategories = categoryService.getIncomeCategories(userAuthHelper.getCurrentUser());
		return ResponseEntity.ok(incomeCategories);
	}

	@GetMapping("/expense")
	public ResponseEntity<List<ExpenseCategory>> getExpenseCategories() {
		List<ExpenseCategory> expenseCategories = categoryService.getExpenseCategories(userAuthHelper.getCurrentUser());
		return ResponseEntity.ok(expenseCategories);
	}

	@PostMapping("/income/create")
	public ResponseEntity<IncomeCategory> saveIncomeCategory(@RequestParam IncomeCategory category) {
		IncomeCategory incomeCategory = categoryService.saveIncomeCategory(category, userAuthHelper.getCurrentUser());
		return ResponseEntity.ok(incomeCategory);
	}

	@PostMapping("/expense/create")
	public ResponseEntity<ExpenseCategory> saveExpenseCategory(@RequestParam ExpenseCategory category) {
		ExpenseCategory expenseCategory = categoryService.saveExpenseCategory(category, userAuthHelper.getCurrentUser());
		return ResponseEntity.ok(expenseCategory);
	}

	@PostMapping("/income/delete/{id}")
	public ResponseEntity<Void> deleteIncomeCategory(@PathVariable Long id) {
		categoryService.deleteIncomeCategory(id, userAuthHelper.getCurrentUserId());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/expense/delete/{id}")
	public ResponseEntity<ExpenseCategory> deleteExpenseCategory(@PathVariable Long id) {
		categoryService.deleteExpenseCategory(id, userAuthHelper.getCurrentUserId());
		return ResponseEntity.noContent().build();
	}

	// END: Endpoints for categories

}
