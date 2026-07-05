package com.tourplanner.exception;

/**
 * Wird geworfen, wenn das Import-JSON ein unbekanntes oder kaputtes Format hat (400 Bad Request).
 */
public class InvalidImportFormatException extends RuntimeException {

    public InvalidImportFormatException(String message) {
        super(message);
    }
}
