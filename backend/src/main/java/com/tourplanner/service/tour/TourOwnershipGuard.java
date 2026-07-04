package com.tourplanner.service.tour;

import com.tourplanner.exception.TourNotFoundException;
import com.tourplanner.model.Tour;
import com.tourplanner.model.User;
import com.tourplanner.repository.TourRepository;
import com.tourplanner.service.shared.SecurityContextService;
import org.springframework.stereotype.Component;

// Prüft Tour-Ownership; gibt 404 statt 403 zurück, um Existenz fremder Ressourcen nicht zu verraten.
@Component
public class TourOwnershipGuard {

    private final TourRepository tourRepository;
    private final SecurityContextService securityContextService;

    public TourOwnershipGuard(TourRepository tourRepository,
                              SecurityContextService securityContextService) {
        this.tourRepository = tourRepository;
        this.securityContextService = securityContextService;
    }

    // Lädt die Tour und stellt sicher, dass sie dem aktuellen User gehört – wirft 404 sonst.
    public Tour requireOwnedTour(long tourId) {
        User currentUser = securityContextService.getCurrentUser();
        return tourRepository.findById(tourId)
                .filter(tour -> isOwnedBy(tour, currentUser))
                .orElseThrow(() -> new TourNotFoundException(tourId));
    }

    public boolean isOwnedBy(Tour tour, User user) {
        return tour.getOwner().getId().equals(user.getId());
    }
}
