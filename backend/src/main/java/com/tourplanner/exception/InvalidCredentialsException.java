package com.tourplanner.exception;

/**
 * Wird geworfen, wenn Login-Credentials ungültig sind (401 Unauthorized).
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
