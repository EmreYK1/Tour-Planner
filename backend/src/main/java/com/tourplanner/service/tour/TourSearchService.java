package com.tourplanner.service.tour;

import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.model.User;
import com.tourplanner.repository.TourLogRepository;
import com.tourplanner.repository.TourRepository;
import com.tourplanner.service.shared.TourAttributeCalculator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

// Sucht Touren per DB-Query (Felder), Batch-Query (Log-Kommentare) und in-memory (berechnete Attribute).
@Service
public class TourSearchService {

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;
    private final TourAttributeCalculator attributeCalculator;

    public TourSearchService(TourRepository tourRepository,
                             TourLogRepository tourLogRepository,
                             TourAttributeCalculator attributeCalculator) {
        this.tourRepository = tourRepository;
        this.tourLogRepository = tourLogRepository;
        this.attributeCalculator = attributeCalculator;
    }

    // Filtert die Touren des Users nach dem Suchbegriff; max. 3 DB-Queries insgesamt.
    public List<Tour> filterByQuery(List<Tour> allOwnedTours, User owner, String query) {
        if (allOwnedTours.isEmpty()) {
            return List.of();
        }

        Set<Long> textMatchIds = tourRepository.searchByTextAndOwner(query, owner)
                .stream().map(Tour::getId).collect(Collectors.toSet());

        List<Long> allIds = allOwnedTours.stream().map(Tour::getId).toList();

        Set<Long> commentMatchIds = tourLogRepository
                .findByTourIdsAndCommentContaining(allIds, query)
                .stream().map(l -> l.getTour().getId()).collect(Collectors.toSet());

        Map<Long, List<TourLog>> logsByTourId = tourLogRepository.findAllByTourIds(allIds)
                .stream().collect(Collectors.groupingBy(l -> l.getTour().getId()));

        String lowerQuery = query.toLowerCase();
        Set<Long> computedMatchIds = allOwnedTours.stream()
                .filter(tour -> matchesComputedAttributes(tour, lowerQuery, logsByTourId))
                .map(Tour::getId)
                .collect(Collectors.toSet());

        return allOwnedTours.stream()
                .filter(t -> textMatchIds.contains(t.getId())
                        || commentMatchIds.contains(t.getId())
                        || computedMatchIds.contains(t.getId()))
                .toList();
    }

    private boolean matchesComputedAttributes(Tour tour, String lowerQuery,
                                              Map<Long, List<TourLog>> logsByTourId) {
        List<TourLog> logs = logsByTourId.getOrDefault(tour.getId(), List.of());
        String childFriendliness = attributeCalculator.computeChildFriendliness(logs);
        String popularity = String.valueOf(attributeCalculator.computePopularity(logs));
        return childFriendliness.toLowerCase().contains(lowerQuery)
                || popularity.contains(lowerQuery);
    }
}
