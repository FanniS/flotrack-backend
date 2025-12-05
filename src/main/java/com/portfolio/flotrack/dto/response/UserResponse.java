package com.portfolio.flotrack.dto.response;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserResponse {
	private Long id;
	private String username;
	private String email;
	private LocalDate createdAt;
}
