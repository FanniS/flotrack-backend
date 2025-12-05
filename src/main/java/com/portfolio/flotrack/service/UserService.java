package com.portfolio.flotrack.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portfolio.flotrack.dto.response.UserResponse;
import com.portfolio.flotrack.model.User;
import com.portfolio.flotrack.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Transactional
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public User findUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}

	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	public UserResponse convertUserToResponse(User user) {
		UserResponse response = new UserResponse();
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setEmail(user.getEmail());
		response.setCreatedAt(user.getCreatedAt());
		return response;
	}
}
	
