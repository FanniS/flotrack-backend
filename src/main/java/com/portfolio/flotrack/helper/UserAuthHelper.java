package com.portfolio.flotrack.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portfolio.flotrack.model.User;
import com.portfolio.flotrack.service.AuthenticationService;

@Service
public class UserAuthHelper {
	@Autowired
	private AuthenticationService authenticationService;

	// START: Helper methods
	public User getCurrentUser() {
		return authenticationService.getCurrentUser();
	}

	public Long getCurrentUserId() {
		return getCurrentUser().getId();
	}

	// END: Helper methods
}
