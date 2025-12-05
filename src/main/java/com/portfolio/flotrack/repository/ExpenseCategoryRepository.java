package com.portfolio.flotrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.flotrack.model.ExpenseCategory;
import com.portfolio.flotrack.model.User;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
	List<ExpenseCategory> findByUserIsNullOrUser(User user);
}
