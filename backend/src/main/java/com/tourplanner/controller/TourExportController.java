package com.tourplanner.controller;

import com.tourplanner.dto.TourExportDto;
import com.tourplanner.service.TourExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourExportController {

    private final TourExportService exportService;

    public TourExportController(TourExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<List<TourExportDto>> exportTours() {
        List<TourExportDto> exportData = exportService.exportAllTours();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tour-planner-export.json\"")
                .body(exportData);
    }
}
