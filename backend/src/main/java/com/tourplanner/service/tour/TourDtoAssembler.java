package com.tourplanner.service.tour;

import com.tourplanner.dto.TourResponse;
import com.tourplanner.mapper.TourMapper;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.repository.TourLogRepository;
import com.tourplanner.service.shared.TourAttributeCalculator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TourDtoAssembler {

    private final TourMapper tourMapper;
    private final TourLogRepository tourLogRepository;
    private final TourAttributeCalculator attributeCalculator;

    public TourDtoAssembler(TourMapper tourMapper,
                            TourLogRepository tourLogRepository,
                            TourAttributeCalculator attributeCalculator) {
        this.tourMapper = tourMapper;
        this.tourLogRepository = tourLogRepository;
        this.attributeCalculator = attributeCalculator;
    }

    // Effiziente Variante für Listen – nutzt vorberechnete Log-Map statt N+1-Queries.
    public TourResponse assemble(Tour tour, Map<Long, List<TourLog>> logsByTourId) {
        List<TourLog> logs = logsByTourId.getOrDefault(tour.getId(), List.of());
        return buildResponse(tour, logs);
    }

    // Einzelabfrage für singuläre Operationen (create, update, findById).
    public TourResponse assemble(Tour tour) {
        List<TourLog> logs = tourLogRepository.findByTourId(tour.getId());
        return buildResponse(tour, logs);
    }

    // Lädt alle Logs für eine Tour-Liste in einer einzigen DB-Query und gruppiert sie nach Tour-ID.
    public Map<Long, List<TourLog>> buildLogMap(List<Tour> tours) {
        if (tours.isEmpty()) {
            return Map.of();
        }
        List<Long> tourIds = tours.stream().map(Tour::getId).toList();
        return tourLogRepository.findAllByTourIds(tourIds).stream()
                .collect(Collectors.groupingBy(log -> log.getTour().getId()));
    }

    private TourResponse buildResponse(Tour tour, List<TourLog> logs) {
        int popularity = attributeCalculator.computePopularity(logs);
        String childFriendliness = attributeCalculator.computeChildFriendliness(logs);
        return tourMapper.toResponse(tour, popularity, childFriendliness);
    }
}
