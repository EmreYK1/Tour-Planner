package com.tourplanner.service;

import com.tourplanner.model.Tour;
import com.tourplanner.dto.TourLogDto;
import com.tourplanner.mapper.TourLogMapper;
import com.tourplanner.model.TourLog;
import com.tourplanner.repository.TourLogRepository;
import com.tourplanner.repository.TourRepository;
import com.tourplanner.model.User;
import com.tourplanner.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
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
    private final UserRepository userRepository;

    public TourLogServiceImpl(TourLogRepository tourLogRepository, TourRepository tourRepository, TourLogMapper tourLogMapper, UserRepository userRepository) {
        this.tourLogRepository = tourLogRepository;
        this.tourRepository = tourRepository;
        this.tourLogMapper = tourLogMapper;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public List<TourLogDto> findByTourId(Long tourId) {
        User currentUser = getCurrentUser();
        if (tourRepository.findByIdAndOwner(tourId, currentUser).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour not found");
        }
        return tourLogRepository.findByTourId(tourId).stream().map(tourLogMapper::toDto).toList();
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public TourLogDto create(Long tourId, TourLogDto dto) {
        User currentUser = getCurrentUser();
        Tour tour = tourRepository.findByIdAndOwner(tourId, currentUser)  
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TourLog entity = tourLogMapper.toNewEntity(dto, tour);
        TourLog saved = tourLogRepository.save(entity);
        return tourLogMapper.toDto(saved);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public TourLogDto update(Long tourId, Long logId, TourLogDto dto) {
        User currentUser = getCurrentUser();
        tourRepository.findByIdAndOwner(tourId, currentUser)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
    @SuppressWarnings("null")
    public void delete(Long tourId, Long logId) {
        User currentUser = getCurrentUser();
        tourRepository.findByIdAndOwner(tourId, currentUser)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TourLog entity = tourLogRepository.findById(logId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!entity.getTour().getId().equals(tourId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        tourLogRepository.delete(entity);
    }
}