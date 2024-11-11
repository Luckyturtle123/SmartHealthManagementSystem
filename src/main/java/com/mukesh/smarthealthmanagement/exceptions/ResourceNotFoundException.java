package com.mukesh.smarthealthmanagement.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructor accepting only a message
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    // Constructor accepting both message and cause
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
