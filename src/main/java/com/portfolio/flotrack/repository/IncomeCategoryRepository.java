package com.portfolio.flotrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.flotrack.model.IncomeCategory;
import com.portfolio.flotrack.model.User;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {
	List<IncomeCategory> findByUserIsNullOrUser(User user);
}
