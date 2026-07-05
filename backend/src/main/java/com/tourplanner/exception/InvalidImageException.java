package com.tourplanner.exception;

/**
 * Wird geworfen, wenn das hochgeladene Bild ungültig ist
 * (falscher MIME-Typ, zu groß, korrupt usw.) — 400 Bad Request.
 */
public class InvalidImageException extends RuntimeException {

    public InvalidImageException(String message) {
        super(message);
    }
}
