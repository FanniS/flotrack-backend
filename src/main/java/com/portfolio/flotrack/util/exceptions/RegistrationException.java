package com.portfolio.flotrack.util.exceptions;

import org.springframework.http.HttpStatus;

public class RegistrationException extends RuntimeException {

	private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

	public HttpStatus getHttpStatus() {
        return httpStatus;
    }

	public RegistrationException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}
}

