// backend/src/main/java/com/tourplanner/service/TourServiceImpl.java
// Orchestriert die Tour-Use-Cases: delegiert an spezialisierte Komponenten.
package com.tourplanner.service;

import com.tourplanner.dto.CreateTourRequest;
import com.tourplanner.dto.TourResponse;
import com.tourplanner.mapper.TourMapper;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.model.User;
import com.tourplanner.repository.TourRepository;
import com.tourplanner.service.shared.SecurityContextService;
import com.tourplanner.service.tour.TourDtoAssembler;
import com.tourplanner.service.tour.TourEnrichmentService;
import com.tourplanner.service.tour.TourOwnershipGuard;
import com.tourplanner.service.tour.TourSearchService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TourServiceImpl implements TourService {

    private static final Logger log = LoggerFactory.getLogger(TourServiceImpl.class);

    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final TourDtoAssembler dtoAssembler;
    private final TourEnrichmentService enrichmentService;
    private final TourOwnershipGuard ownershipGuard;
    private final TourSearchService searchService;
    private final SecurityContextService securityContextService;

    public TourServiceImpl(TourRepository tourRepository,
                           TourMapper tourMapper,
                           TourDtoAssembler dtoAssembler,
                           TourEnrichmentService enrichmentService,
                           TourOwnershipGuard ownershipGuard,
                           TourSearchService searchService,
                           SecurityContextService securityContextService) {
        this.tourRepository = tourRepository;
        this.tourMapper = tourMapper;
        this.dtoAssembler = dtoAssembler;
        this.enrichmentService = enrichmentService;
        this.ownershipGuard = ownershipGuard;
        this.searchService = searchService;
        this.securityContextService = securityContextService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourResponse> findAll() {
        List<Tour> tours = toursOfCurrentUser();
        log.info("Fetching all tours for current user – found {}", tours.size());
        // Alle Logs in einer einzigen Query laden – kein N+1
        Map<Long, List<TourLog>> logMap = dtoAssembler.buildLogMap(tours);
        return tours.stream()
                .map(tour -> dtoAssembler.assemble(tour, logMap))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourResponse> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        log.info("Searching tours with query='{}'", query);
        User currentUser = securityContextService.getCurrentUser();
        List<Tour> ownedTours = tourRepository.findByOwner(currentUser);
        List<Tour> matched = searchService.filterByQuery(ownedTours, currentUser, query);
        // Log-Map für die gefilterten Touren aufbauen – kein N+1
        Map<Long, List<TourLog>> logMap = dtoAssembler.buildLogMap(matched);
        return matched.stream()
                .map(tour -> dtoAssembler.assemble(tour, logMap))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TourResponse> findById(long id) {
        User currentUser = securityContextService.getCurrentUser();
        return tourRepository.findById(id)
                .filter(tour -> ownershipGuard.isOwnedBy(tour, currentUser))
                .map(dtoAssembler::assemble);
    }

    @Override
    @Transactional
    public TourResponse create(CreateTourRequest request) {
        Tour entity = tourMapper.toNewEntity(request);
        entity.setOwner(securityContextService.getCurrentUser());
        enrichmentService.enrichWithRouteData(entity, request);
        TourResponse response = dtoAssembler.assemble(tourRepository.save(entity));
        log.info("Tour created with id={}, name='{}'", response.id(), response.name());
        return response;
    }

    @Override
    @Transactional
    public TourResponse update(long id, CreateTourRequest request) {
        Tour entity = ownershipGuard.requireOwnedTour(id);
        tourMapper.apply(request, entity);
        enrichmentService.enrichWithRouteData(entity, request);
        TourResponse response = dtoAssembler.assemble(tourRepository.save(entity));
        log.info("Tour updated with id={}", id);
        return response;
    }

    @Override
    @Transactional
    public TourResponse updateImage(long id, String imageUrl) {
        Tour entity = ownershipGuard.requireOwnedTour(id);
        entity.setImage(imageUrl);
        TourResponse response = dtoAssembler.assemble(tourRepository.save(entity));
        log.info("Image updated for tour id={}", id);
        return response;
    }

    @Override
    @Transactional
    public void delete(long id) {
        tourRepository.delete(ownershipGuard.requireOwnedTour(id));
        log.info("Tour deleted with id={}", id);
    }

    private List<Tour> toursOfCurrentUser() {
        return tourRepository.findByOwner(securityContextService.getCurrentUser());
    }
}
