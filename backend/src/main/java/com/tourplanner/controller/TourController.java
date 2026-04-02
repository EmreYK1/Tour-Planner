// backend/src/main/java/com/tourplanner/controller/TourController.java
// REST-API für Touren: Liste, Einzelabruf und Anlegen.
package com.tourplanner.controller;

import com.tourplanner.dto.TourDto;
import com.tourplanner.service.TourService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public List<TourDto> list() {
        return tourService.findAll();
    }

    @GetMapping("/{id}")
    public TourDto get(@PathVariable long id) {
        return tourService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TourDto create(@Valid @RequestBody TourDto tour) {
        return tourService.create(tour);
    }
}
