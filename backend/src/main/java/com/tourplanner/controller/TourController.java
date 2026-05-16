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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.tourplanner.service.ImageStorageService;
import java.io.IOException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;
    private final ImageStorageService imageStorageService;

    public TourController(TourService tourService, ImageStorageService imageStorageService) {
        this.tourService = tourService;
        this.imageStorageService = imageStorageService;
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

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable long id, @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageStorageService.saveImage(file);
            tourService.updateImage(id, imageUrl);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Speichern der Bilddatei");
        }
    }

    @PutMapping("/{id}")
    public TourDto update(@PathVariable long id, @Valid @RequestBody TourDto tour) {
        return tourService.update(id, tour);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        tourService.delete(id);
    }
}
