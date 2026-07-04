package com.tourplanner.service;

import com.tourplanner.dto.TourExportDto;
import com.tourplanner.dto.TourLogDto;
import com.tourplanner.exception.InvalidImportFormatException;
import com.tourplanner.mapper.TourLogMapper;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.model.User;
import com.tourplanner.repository.TourLogRepository;
import com.tourplanner.repository.TourRepository;
import com.tourplanner.service.shared.SecurityContextService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TourImportServiceImpl implements TourImportService {

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;
    private final TourLogMapper tourLogMapper;
    private final SecurityContextService securityContextService;

    public TourImportServiceImpl(TourRepository tourRepository,
                                 TourLogRepository tourLogRepository,
                                 TourLogMapper tourLogMapper,
                                 SecurityContextService securityContextService) {
        this.tourRepository = tourRepository;
        this.tourLogRepository = tourLogRepository;
        this.tourLogMapper = tourLogMapper;
        this.securityContextService = securityContextService;
    }

    @Override
    @Transactional
    public void importTours(List<TourExportDto> importData) {
        if (importData == null || importData.isEmpty()) {
            throw new InvalidImportFormatException("Die Import-Datei ist leer oder hat ein ungültiges Format.");
        }

        User currentUser = securityContextService.getCurrentUser();

        for (TourExportDto dto : importData) {
            // Einfache Validierung, um kaputte Daten zu erkennen
            if (dto.name() == null || dto.name().isBlank()) {
                throw new InvalidImportFormatException("Eine Tour in der Datei hat keinen Namen.");
            }

            // 1. Tour Entity erstellen und mappen
            Tour tour = new Tour();
            tour.setName(dto.name());
            tour.setDescription(dto.description());
            tour.setFromLocation(dto.fromPoint());
            tour.setToLocation(dto.toPoint());
            tour.setTransportType(dto.transportType());
            tour.setDistance(dto.distance());
            tour.setEstimatedTime(dto.estimatedTime());
            tour.setImage(dto.image());
            tour.setRouteGeometry(dto.routeGeometry());
            tour.setOwner(currentUser); // Die Tour gehört jetzt dem aktuellen Nutzer

            // 2. Tour speichern (damit sie eine ID bekommt)
            Tour savedTour = tourRepository.save(tour);

            // 3. Wenn es Logs gibt, diese auch mappen und speichern
            if (dto.logs() != null) {
                for (TourLogDto logDto : dto.logs()) {
                    TourLog log = tourLogMapper.toNewEntity(logDto, savedTour);
                    tourLogRepository.save(log);
                }
            }
        }
    }
}
