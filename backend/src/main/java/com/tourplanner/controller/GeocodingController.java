// backend/src/main/java/com/tourplanner/controller/GeocodingController.java
// REST-Endpoint für Geocoding: wandelt eine Freitextadresse in lon/lat-Koordinaten um.
package com.tourplanner.controller;

import com.tourplanner.client.OrsClient;
import com.tourplanner.dto.GeocodingResultDto;
import com.tourplanner.exception.OrsApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/geocode")
public class GeocodingController {

    private final OrsClient orsClient;

    public GeocodingController(OrsClient orsClient) {
        this.orsClient = orsClient;
    }

    /**
     * Wandelt eine Freitextadresse in geografische Koordinaten um.
     *
     * Beispiel: GET /api/geocode?q=Wien
     * Antwort:  { "lon": 16.3738, "lat": 48.2082 }
     *
     * @param q Adresse als Freitext (z.B. "Wien" oder "Stephansplatz, Wien")
     * @return lon/lat des ersten ORS-Treffers
     */
    @GetMapping
    public GeocodingResultDto geocode(@RequestParam String q) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query parameter 'q' must not be empty");
        }
        try {
            return orsClient.fetchGeocode(q);
        } catch (OrsApiException e) {
            // Adresse nicht gefunden oder ORS nicht erreichbar → 404 mit verständlicher Meldung
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found: " + q);
        }
    }
}
