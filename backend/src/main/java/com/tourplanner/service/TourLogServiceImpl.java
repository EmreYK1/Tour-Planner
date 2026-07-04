package com.tourplanner.service;

import com.tourplanner.dto.TourLogDto;
import com.tourplanner.mapper.TourLogMapper;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.repository.TourLogRepository;
import com.tourplanner.service.tourlog.TourLogOwnershipGuard;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TourLogServiceImpl implements TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourLogMapper tourLogMapper;
    private final TourLogOwnershipGuard ownershipGuard;

    public TourLogServiceImpl(TourLogRepository tourLogRepository,
                              TourLogMapper tourLogMapper,
                              TourLogOwnershipGuard ownershipGuard) {
        this.tourLogRepository = tourLogRepository;
        this.tourLogMapper = tourLogMapper;
        this.ownershipGuard = ownershipGuard;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public List<TourLogDto> findByTourId(Long tourId) {
        ownershipGuard.requireOwnedTour(tourId);
        return tourLogRepository.findByTourId(tourId).stream()
                .map(tourLogMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public TourLogDto create(Long tourId, TourLogDto dto) {
        Tour tour = ownershipGuard.requireOwnedTour(tourId);
        TourLog entity = tourLogMapper.toNewEntity(dto, tour);
        return tourLogMapper.toDto(tourLogRepository.save(entity));
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public TourLogDto update(Long tourId, Long logId, TourLogDto dto) {
        TourLog entity = ownershipGuard.requireLogOfOwnedTour(tourId, logId);
        tourLogMapper.apply(dto, entity);
        return tourLogMapper.toDto(tourLogRepository.save(entity));
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void delete(Long tourId, Long logId) {
        TourLog entity = ownershipGuard.requireLogOfOwnedTour(tourId, logId);
        tourLogRepository.delete(entity);
    }
}