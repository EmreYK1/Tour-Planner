package com.tourplanner.service.tourlog;

import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.repository.TourLogRepository;
import com.tourplanner.service.tour.TourOwnershipGuard;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

// Prüft Ownership bei TourLog-Zugriffen; delegiert Tour-Ownership an TourOwnershipGuard.
@Component
public class TourLogOwnershipGuard {

    private final TourLogRepository tourLogRepository;
    private final TourOwnershipGuard tourOwnershipGuard;

    public TourLogOwnershipGuard(TourLogRepository tourLogRepository,
                                 TourOwnershipGuard tourOwnershipGuard) {
        this.tourLogRepository = tourLogRepository;
        this.tourOwnershipGuard = tourOwnershipGuard;
    }

    public Tour requireOwnedTour(Long tourId) {
        return tourOwnershipGuard.requireOwnedTour(tourId);
    }

    // Lädt den Log und stellt sicher, dass er zur angegebenen eigenen Tour gehört.
    public TourLog requireLogOfOwnedTour(Long tourId, Long logId) {
        requireOwnedTour(tourId);
        TourLog entity = tourLogRepository.findById(logId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!entity.getTour().getId().equals(tourId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return entity;
    }
}
