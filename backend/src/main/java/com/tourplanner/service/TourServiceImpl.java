// backend/src/main/java/com/tourplanner/service/TourServiceImpl.java
// Implementiert TourService mit JPA-Repository und TourMapper.
package com.tourplanner.service;

import com.tourplanner.dto.TourDto;
import com.tourplanner.mapper.TourMapper;
import com.tourplanner.model.Tour;
import com.tourplanner.repository.TourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final TourMapper tourMapper;

    public TourServiceImpl(TourRepository tourRepository, TourMapper tourMapper) {
        this.tourRepository = tourRepository;
        this.tourMapper = tourMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourDto> findAll() {
        return tourRepository.findAll().stream().map(tourMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TourDto> findById(long id) {
        return tourRepository.findById(id).map(tourMapper::toDto);
    }

    @Override
    @Transactional
    public TourDto create(TourDto tour) {
        Tour entity = tourMapper.toNewEntity(tour);
        Tour saved = tourRepository.save(entity);
        return tourMapper.toDto(saved);
    }
}
