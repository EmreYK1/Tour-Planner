package com.tourplanner.exception;

/**
 * Wird geworfen, wenn ein TourLog mit der angegebenen ID nicht existiert
 * oder dem aktuellen Nutzer nicht gehört (404 statt 403 verhindert,
 * dass die Existenz fremder Ressourcen verraten wird).
 */
public class TourLogNotFoundException extends RuntimeException {

    public TourLogNotFoundException(long logId) {
        super("TourLog nicht gefunden: " + logId);
    }
}
