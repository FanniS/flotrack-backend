package com.portfolio.flotrack.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.flotrack.dto.request.LoginRequest;
import com.portfolio.flotrack.dto.request.RegisterRequest;
import com.portfolio.flotrack.dto.response.LoginResponse;
import com.portfolio.flotrack.dto.response.UserResponse;
import com.portfolio.flotrack.model.User;
import com.portfolio.flotrack.service.AuthenticationService;
import com.portfolio.flotrack.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationService authenticationService;
	@GetMapping("/users")
	public List<UserResponse> getUsers() {
		List<User> users = userService.findAllUsers();
		return users.stream()
				.map(userService::convertUserToResponse)
				.toList();
	}

	// START: Registration and login endpoint
	
	@Valid
	@PostMapping("/auth/register")
	public ResponseEntity<LoginResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest){
		LoginResponse response = authenticationService.registerNewUserAccount(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Valid
	@PostMapping("/auth/login")
	public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
		LoginResponse response = authenticationService.loginUser(loginRequest);
		return ResponseEntity.ok(response);
	}

	//END: Registration and login endpoint
}
