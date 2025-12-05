package com.portfolio.flotrack.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portfolio.flotrack.model.ExpenseCategory;
import com.portfolio.flotrack.model.IncomeCategory;
import com.portfolio.flotrack.model.User;
import com.portfolio.flotrack.repository.ExpenseCategoryRepository;
import com.portfolio.flotrack.repository.IncomeCategoryRepository;
import com.portfolio.flotrack.util.exceptions.ResourceNotFoundException;
import com.portfolio.flotrack.util.exceptions.UnauthorizedAccessException;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {
	@Autowired
	private IncomeCategoryRepository incomeCategoryRepository;

	@Autowired
	private ExpenseCategoryRepository expenseCategoryRepository;

	// START: CRUD methods for categories
	public List<IncomeCategory> getIncomeCategories(User user) {
		return incomeCategoryRepository.findByUserIsNullOrUser(user);
	}

	public List<ExpenseCategory> getExpenseCategories(User user) {
		return expenseCategoryRepository.findByUserIsNullOrUser(user);
	}

	@Transactional
	public IncomeCategory saveIncomeCategory(IncomeCategory category, User currentUser) {
		IncomeCategory newCategory = category;
		newCategory.setUser(currentUser);
		return incomeCategoryRepository.save(newCategory);
	}

	@Transactional
	public ExpenseCategory saveExpenseCategory(ExpenseCategory category, User currentUser) {
		ExpenseCategory newCategory = category;
		newCategory.setUser(currentUser);
		return expenseCategoryRepository.save(newCategory);
	}

	@Transactional
	public void deleteIncomeCategory(Long incomeCategoryId, Long currentUserId) {
		IncomeCategory categoryToDelete = incomeCategoryRepository.findById(incomeCategoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Income Category not found"));
		if (!categoryToDelete.getUser().getId().equals(currentUserId)) {
			throw new UnauthorizedAccessException("You can only delete your own categories!");
		}
		incomeCategoryRepository.delete(categoryToDelete);
	}

	@Transactional
	public void deleteExpenseCategory(Long expenseCategoryId, Long currentUserId) {
		ExpenseCategory categoryToDelete = expenseCategoryRepository.findById(expenseCategoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Expense Category not found"));
		if (!categoryToDelete.getUser().getId().equals(currentUserId)) {
			throw new UnauthorizedAccessException("You can only delete your own categories!");
		}
		expenseCategoryRepository.delete(categoryToDelete);
	}

	// END: CRUD methods for categories
}
