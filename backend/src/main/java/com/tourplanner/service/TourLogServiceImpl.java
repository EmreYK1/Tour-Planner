package com.tourplanner.service;

import com.tourplanner.model.Tour;
import com.tourplanner.dto.TourLogDto;
import com.tourplanner.mapper.TourLogMapper;
import com.tourplanner.model.TourLog;
import com.tourplanner.repository.TourLogRepository;
import com.tourplanner.repository.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TourLogServiceImpl implements TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;
    private final TourLogMapper tourLogMapper;

    public TourLogServiceImpl(TourLogRepository tourLogRepository, TourRepository tourRepository, TourLogMapper tourLogMapper) {
        this.tourLogRepository = tourLogRepository;
        this.tourRepository = tourRepository;
        this.tourLogMapper = tourLogMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourLogDto> findByTourId(Long tourId) {
        if (!tourRepository.existsById(tourId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return tourLogRepository.findByTourId(tourId).stream().map(tourLogMapper::toDto).toList();
    }

    @Override
    @Transactional
    public TourLogDto create(Long tourId, TourLogDto dto) {
        Tour tour = tourRepository.findById(tourId)  
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TourLog entity = tourLogMapper.toNewEntity(dto, tour);
        TourLog saved = tourLogRepository.save(entity);
        return tourLogMapper.toDto(saved);
    }

    @Override
    @Transactional
    public TourLogDto update(Long tourId, Long logId, TourLogDto dto) {
        TourLog entity = tourLogRepository.findById(logId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!entity.getTour().getId().equals(tourId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        tourLogMapper.apply(dto, entity);
        TourLog saved = tourLogRepository.save(entity);
        return tourLogMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long tourId, Long logId) {
        TourLog entity = tourLogRepository.findById(logId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!entity.getTour().getId().equals(tourId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        tourLogRepository.delete(entity);
    }
}