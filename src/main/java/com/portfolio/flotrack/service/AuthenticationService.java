package com.portfolio.flotrack.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.portfolio.flotrack.dto.request.LoginRequest;
import com.portfolio.flotrack.dto.request.RegisterRequest;
import com.portfolio.flotrack.dto.response.LoginResponse;
import com.portfolio.flotrack.model.User;
import com.portfolio.flotrack.repository.UserRepository;
import com.portfolio.flotrack.util.exceptions.RegistrationException;
import com.portfolio.flotrack.util.jwt.JwtService;

import jakarta.transaction.Transactional;

@Service
public class AuthenticationService {
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails user = (UserDetails) authentication.getPrincipal();
		return userRepository.findByEmail(user.getUsername())
				.stream()
				.findFirst()
				.orElse(null);
	}

	@Transactional
	public LoginResponse registerNewUserAccount(RegisterRequest registerUserRequest) throws RegistrationException {
		if (emailExist(registerUserRequest.getEmail())) {
			throw new RegistrationException(HttpStatus.CONFLICT, "There is an account with that email adress: " + registerUserRequest.getEmail());
		}

		User user = new User();
		user.setEmail(registerUserRequest.getEmail());
		user.setUsername(registerUserRequest.getUsername());
		user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
		user.setEnabled(true);

		userRepository.save(user);
		
		return new LoginResponse(jwtService.generateToken(user), user.getEmail(), "Registration successful. Welcome " + user.getUsername() + "!");
	}

	private boolean emailExist(String email) {
		return userRepository.findByEmail(email).size() > 0;
	}

	public LoginResponse loginUser(LoginRequest loginRequest) {
		User user = userRepository.findByEmail(loginRequest.getEmail())
				.stream()
				.findFirst()
				.orElseThrow(() -> new RegistrationException(HttpStatus.BAD_REQUEST, "Invalid email or password"));

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new RegistrationException(HttpStatus.BAD_REQUEST, "Invalid email or password");
		}

		String token = jwtService.generateToken(user);

		return new LoginResponse(token, user.getEmail(), "Login successful. Welcome back " + user.getUsername() + "!");
	}
}
