// backend/src/main/java/com/tourplanner/controller/TourController.java
// REST-Endpunkte für Tour-CRUD: Lesen, Anlegen, Aktualisieren, Löschen.
package com.tourplanner.controller;

import com.tourplanner.dto.CreateTourRequest;
import com.tourplanner.dto.TourResponse;
import com.tourplanner.service.TourService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public List<TourResponse> list(@RequestParam(required = false) String search) {
        return tourService.search(search);
    }

    @GetMapping("/{id}")
    public TourResponse get(@PathVariable long id) {
        return tourService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TourResponse create(@Valid @RequestBody CreateTourRequest request) {
        return tourService.create(request);
    }

    @PutMapping("/{id}")
    public TourResponse update(@PathVariable long id, @Valid @RequestBody CreateTourRequest request) {
        return tourService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        tourService.delete(id);
    }
}
