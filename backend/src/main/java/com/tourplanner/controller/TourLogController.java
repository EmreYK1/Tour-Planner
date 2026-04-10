package com.tourplanner.controller;

import com.tourplanner.dto.TourLogDto;
import com.tourplanner.service.TourLogService;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/tours/{tourId}/logs")
public class TourLogController {

    private final TourLogService tourLogService;

    public TourLogController(TourLogService tourLogService) {
        this.tourLogService = tourLogService;
    }

    @GetMapping
    public List<TourLogDto> get(@PathVariable Long tourId) {
        return tourLogService.findByTourId(tourId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TourLogDto create(@PathVariable Long tourId, @Valid @RequestBody TourLogDto tourLog) {
        return tourLogService.create(tourId, tourLog);
    }

    @PutMapping("/{logId}")
    public TourLogDto update(@PathVariable Long tourId, @PathVariable Long logId, @Valid @RequestBody TourLogDto tourLog) {
        return tourLogService.update(tourId, logId, tourLog);
    }

    @DeleteMapping("/{logId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long tourId, @PathVariable Long logId) {
        tourLogService.delete(tourId, logId);
    }
}