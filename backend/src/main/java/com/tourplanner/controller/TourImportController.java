package com.tourplanner.controller;

import com.tourplanner.dto.TourExportDto;
import com.tourplanner.service.TourImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourImportController {

    private final TourImportService importService;

    public TourImportController(TourImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/import")
    public ResponseEntity<Void> importTours(@RequestBody List<TourExportDto> importData) {
        importService.importTours(importData);
        return ResponseEntity.ok().build();
    }
}
