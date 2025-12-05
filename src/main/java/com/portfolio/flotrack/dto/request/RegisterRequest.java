package com.portfolio.flotrack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {
	@NotBlank (message = "Email is mandatory")
	@Email (message = "Email should be valid")
	private String email;
	@NotBlank (message = "Password is mandatory")
	@Size(min=6, message = "Password must be at least 6 characters long")
	private String password;
	@NotBlank (message = "Username is mandatory")
	@Size(min=3, max=20, message = "Username must be between 3 and 20 characters long")
	private String username;

}
