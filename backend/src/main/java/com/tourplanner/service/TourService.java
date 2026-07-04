// backend/src/main/java/com/tourplanner/service/TourService.java
// Schnittstelle der Anwendungslogik für Touren (lesen, anlegen, aktualisieren, löschen).
package com.tourplanner.service;

import com.tourplanner.dto.CreateTourRequest;
import com.tourplanner.dto.TourResponse;

import java.util.List;
import java.util.Optional;

public interface TourService {

    List<TourResponse> findAll();

    List<TourResponse> search(String query);

    Optional<TourResponse> findById(long id);

    TourResponse create(CreateTourRequest request);

    TourResponse update(long id, CreateTourRequest request);

    TourResponse updateImage(long id, String imageUrl);

    void delete(long id);
}
