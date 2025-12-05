package com.portfolio.flotrack.util.exceptions;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
	Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

	@ExceptionHandler(RegistrationException.class)
    public ResponseEntity<?> handleException(RegistrationException e) {
		logger.severe("RegistrationException occurred: " + e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
		logger.severe("MethodArgumentNotValidException occurred: " + e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid registration data: " + e.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.reduce((msg1, msg2) -> msg1 + ", " + msg2)
				.orElse("Invalid input"));
	}

	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<?> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
		logger.severe("UnauthorizedAccessException occurred: " + e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
		logger.severe("ResourceNotFoundException occurred: " + e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}
}
