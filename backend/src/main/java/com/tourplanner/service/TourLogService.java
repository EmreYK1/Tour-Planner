package com.tourplanner.service;

import com.tourplanner.dto.TourLogDto;

import java.util.List;

public interface TourLogService {
    List<TourLogDto> findByTourId(Long tourId);
    TourLogDto create(Long tourId, TourLogDto dto);
    TourLogDto update(Long tourId, Long logId, TourLogDto dto);
    void delete(Long tourId, Long logId);
}