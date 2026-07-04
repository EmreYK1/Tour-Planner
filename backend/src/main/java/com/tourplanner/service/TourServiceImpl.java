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
import com.tourplanner.model.User;
import com.tourplanner.repository.TourRepository;
import com.tourplanner.repository.UserRepository;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class TourServiceImpl implements TourService {

    private static final Logger log = LoggerFactory.getLogger(TourServiceImpl.class);

    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final OrsClient orsClient;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;


    public TourServiceImpl(TourRepository tourRepository, TourMapper tourMapper,
                           OrsClient orsClient, ObjectMapper objectMapper, UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.tourMapper = tourMapper;
        this.orsClient = orsClient;
        this.objectMapper = objectMapper;
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
    public List<TourDto> findAll() {
        User currentUser = getCurrentUser();
        return tourRepository.findByOwner(currentUser).stream().map(tourMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TourDto> findById(long id) {
        User currentUser = getCurrentUser();
        return tourRepository.findByIdAndOwner(id, currentUser).map(tourMapper::toDto);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public TourDto create(TourDto tour) {
        User currentUser = getCurrentUser();
        Tour entity = tourMapper.toNewEntity(tour);
        entity.setOwner(currentUser);
        enrichWithOrsData(entity, tour);
        Tour saved = tourRepository.save(entity);
        return tourMapper.toDto(saved);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public TourDto update(long id, TourDto dto) {
        User currentUser = getCurrentUser();
        Tour entity = tourRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tourMapper.apply(dto, entity);
        enrichWithOrsData(entity, dto);
        Tour saved = tourRepository.save(entity);
        return tourMapper.toDto(saved);
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
        User currentUser = getCurrentUser();
        Tour entity = tourRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setImage(imageUrl);
        Tour saved = tourRepository.save(entity);
        return tourMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(long id) {
        User currentUser = getCurrentUser();
        Tour entity = tourRepository.findByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tourRepository.delete(entity);
    }
}
