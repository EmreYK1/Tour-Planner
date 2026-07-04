// backend/src/main/java/com/tourplanner/controller/TourImageController.java
// REST-Endpunkt ausschließlich für den Upload von Tour-Bildern.
package com.tourplanner.controller;

import com.tourplanner.dto.TourResponse;
import com.tourplanner.service.ImageStorageService;
import com.tourplanner.service.TourService;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

// Verarbeitet ausschließlich Upload und Verknüpfung von Tour-Bildern.
@RestController
@RequestMapping("/api/tours")
public class TourImageController {

    private final TourService tourService;
    private final ImageStorageService imageStorageService;

    public TourImageController(TourService tourService, ImageStorageService imageStorageService) {
        this.tourService = tourService;
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<TourResponse> uploadImage(@PathVariable long id,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageStorageService.saveImage(file);
            TourResponse updated = tourService.updateImage(id, imageUrl);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Speichern der Bilddatei");
        }
    }
}
