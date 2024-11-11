package com.mukesh.smarthealthmanagement.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Default constructor
    public ResourceAlreadyExistsException() {
        super();
    }

    // Constructor that accepts a custom message
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    // Constructor that accepts a custom message and cause
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public ResourceAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
