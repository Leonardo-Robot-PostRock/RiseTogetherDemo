package com.ITJobsBackend.shared.domain.exceptions;

public class ValidationException extends DomainException {
    private static final long serialVersionUID = 1L;

	public ValidationException(String message) {
        super(message);
    }
}
