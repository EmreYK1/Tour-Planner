// backend/src/main/java/com/tourplanner/service/TourService.java
// Schnittstelle der Anwendungslogik für Touren (lesen, anlegen).
package com.tourplanner.service;

import com.tourplanner.dto.TourDto;

import java.util.List;
import java.util.Optional;

public interface TourService {

    List<TourDto> findAll();

    Optional<TourDto> findById(long id);

    TourDto create(TourDto tour);

    TourDto update(long id, TourDto dto);
}
