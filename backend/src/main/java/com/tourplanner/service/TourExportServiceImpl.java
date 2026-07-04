package com.tourplanner.service;

import com.tourplanner.dto.TourExportDto;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.repository.TourRepository;
import com.tourplanner.service.shared.SecurityContextService;
import com.tourplanner.service.tour.TourDtoAssembler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class TourExportServiceImpl implements TourExportService {

    private final TourRepository tourRepository;
    private final TourDtoAssembler dtoAssembler;
    private final SecurityContextService securityContextService;

    public TourExportServiceImpl(TourRepository tourRepository,
                                 TourDtoAssembler dtoAssembler,
                                 SecurityContextService securityContextService) {
        this.tourRepository = tourRepository;
        this.dtoAssembler = dtoAssembler;
        this.securityContextService = securityContextService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourExportDto> exportAllTours() {
        List<Tour> tours = tourRepository.findByOwner(securityContextService.getCurrentUser());
        Map<Long, List<TourLog>> logMap = dtoAssembler.buildLogMap(tours);
        
        return tours.stream()
                .map(tour -> dtoAssembler.assembleExport(tour, logMap))
                .toList();
    }
}
