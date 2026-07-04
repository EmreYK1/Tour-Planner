// backend/src/main/java/com/tourplanner/service/TourServiceImpl.java
// Implementiert TourService mit JPA-Repository und TourMapper.
package com.tourplanner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourplanner.client.OrsClient;
import com.tourplanner.client.OrsRouteResult;
import com.tourplanner.dto.TourDto;
import com.tourplanner.mapper.TourMapper;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.repository.TourLogRepository;
import com.tourplanner.repository.TourRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TourServiceImpl implements TourService {

    private static final Logger log = LoggerFactory.getLogger(TourServiceImpl.class);

    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final OrsClient orsClient;
    private final ObjectMapper objectMapper;
    private final TourLogRepository tourLogRepository;

    public TourServiceImpl(TourRepository tourRepository, TourMapper tourMapper,
                           OrsClient orsClient, ObjectMapper objectMapper, TourLogRepository tourLogRepository) {
        this.tourRepository = tourRepository;
        this.tourMapper = tourMapper;
        this.orsClient = orsClient;
        this.objectMapper = objectMapper;
        this.tourLogRepository = tourLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourDto> findAll() {
        return tourRepository.findAll().stream().map(this::toDtoWithComputed).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourDto> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        
        String lowerQuery = query.toLowerCase();
        
        // 1. DB-Suche auf Tour-Feldern
        List<Tour> matchedByTour = tourRepository.searchByText(query);
        Set<Long> matchedTourIds = matchedByTour.stream().map(Tour::getId).collect(Collectors.toSet());
        
        List<TourDto> results = new ArrayList<>();
        List<Tour> allTours = tourRepository.findAll();
        
        for (Tour tour : allTours) {
            boolean matches = false;
            
            // 1. Match from DB search
            if (matchedTourIds.contains(tour.getId())) {
                matches = true;
            }
            
            // 2. Tours einschließen deren Logs den Begriff im Comment enthalten
            if (!matches) {
                List<TourLog> matchedLogs = tourLogRepository.findByTourIdAndCommentContainingIgnoreCase(tour.getId(), query);
                if (!matchedLogs.isEmpty()) {
                    matches = true;
                }
            }
            
            // Compute DTO (we need it for step 3 and to return)
            TourDto dto = toDtoWithComputed(tour);
            
            // 3. Berechnete Attribute (childFriendliness, popularity-Label) auf Match prüfen
            if (!matches) {
                if (dto.childFriendliness().toLowerCase().contains(lowerQuery) || 
                    String.valueOf(dto.popularity()).contains(lowerQuery)) {
                    matches = true;
                }
            }
            
            // 4. Duplikate entfernen (Set) - handled by the fact that we iterate allTours exactly once
            if (matches) {
                results.add(dto);
            }
        }
        
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TourDto> findById(long id) {
        return tourRepository.findById(id).map(this::toDtoWithComputed);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public TourDto create(TourDto tour) {
        Tour entity = tourMapper.toNewEntity(tour);
        enrichWithOrsData(entity, tour);
        Tour saved = tourRepository.save(entity);
        return toDtoWithComputed(saved);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public TourDto update(long id, TourDto dto) {
        Tour entity = tourRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tourMapper.apply(dto, entity);
        enrichWithOrsData(entity, dto);
        Tour saved = tourRepository.save(entity);
        return toDtoWithComputed(saved);
    }

    private void enrichWithOrsData(Tour entity, TourDto dto) {
        if (dto.fromLon() == null || dto.fromLat() == null
                || dto.toLon() == null || dto.toLat() == null) {
            return;
        }
        try {
            List<List<Double>> coords = List.of(
                    List.of(dto.fromLon(), dto.fromLat()),
                    List.of(dto.toLon(), dto.toLat()));
            OrsRouteResult result = orsClient.fetchRoute(dto.transportType(), coords);
            entity.setDistance(result.distanceMeters() / 1000.0);
            entity.setEstimatedTime((long) result.durationSeconds());
            entity.setRouteGeometry(objectMapper.writeValueAsString(result.geometry()));
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize ORS geometry: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("ORS route fetch failed, keeping user-provided values: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public TourDto updateImage(long id, String imageUrl) {
        Tour entity = tourRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setImage(imageUrl);
        Tour saved = tourRepository.save(entity);
        return toDtoWithComputed(saved);
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (!tourRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        tourRepository.deleteById(id);
    }

    private TourDto toDtoWithComputed(Tour entity) {
        List<TourLog> logs = tourLogRepository.findByTourId(entity.getId());
        int popularity = computePopularity(entity.getId());
        String childFriendliness = computeChildFriendliness(logs);
        return tourMapper.toDto(entity, popularity, childFriendliness);
    }

    private int computePopularity(long tourId) {
        return tourLogRepository.findByTourId(tourId).size();
    }

    private String computeChildFriendliness(List<TourLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return "Niedrig";
        }
        double avgDifficulty = logs.stream().mapToInt(TourLog::getDifficulty).average().orElse(0.0);
        double avgDistance = logs.stream().mapToDouble(TourLog::getTotalDistance).average().orElse(0.0);
        double avgTime = logs.stream().mapToLong(TourLog::getTotalTime).average().orElse(0.0);

        if (avgDifficulty <= 2 && avgDistance <= 10.0 && avgTime <= 120.0) {
            return "Hoch";
        }
        if (avgDifficulty <= 3 && avgDistance <= 25.0) {
            return "Mittel";
        }
        return "Niedrig";
    }
}
