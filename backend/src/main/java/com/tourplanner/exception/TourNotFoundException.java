package com.tourplanner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 404 statt 403 – verhindert, dass die Existenz fremder Ressourcen verraten wird.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TourNotFoundException extends RuntimeException {

    public TourNotFoundException(long tourId) {
        super("Tour nicht gefunden: " + tourId);
    }
}
