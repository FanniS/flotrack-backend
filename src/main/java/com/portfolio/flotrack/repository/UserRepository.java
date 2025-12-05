package com.portfolio.flotrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.flotrack.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByEmail(String email);
}
