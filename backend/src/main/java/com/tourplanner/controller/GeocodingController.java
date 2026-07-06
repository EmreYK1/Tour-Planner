// backend/src/main/java/com/tourplanner/controller/GeocodingController.java
// REST-Endpoint für Geocoding: wandelt eine Freitextadresse in lon/lat-Koordinaten um.
package com.tourplanner.controller;

import com.tourplanner.dto.GeocodingResultDto;
import com.tourplanner.service.GeocodingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/geocode")
public class GeocodingController {

    private final GeocodingService geocodingService;

    public GeocodingController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    // Wandelt eine Freitextadresse in lon/lat um; GET /api/geocode?q=Wien
    @GetMapping
    public GeocodingResultDto geocode(@RequestParam String q) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query parameter 'q' must not be empty");
        }
        return geocodingService.geocode(q);
    }
}
