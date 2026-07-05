package com.tourplanner.exception;

import java.time.Instant;

/**
 * Einheitliches Fehler-DTO für alle API-Fehlerantworten.
 * Immutable record: kein Setter, kein Builder-Boilerplate nötig.
 */
public record ErrorResponse(Instant timestamp, int status, String message) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(Instant.now(), status, message);
    }
}
