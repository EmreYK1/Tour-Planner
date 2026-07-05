package com.tourplanner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tourplanner.dto.TourLogDto;
import com.tourplanner.exception.TourNotFoundException;
import com.tourplanner.mapper.TourLogMapper;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.model.TransportType;
import com.tourplanner.model.User;
import com.tourplanner.repository.TourLogRepository;
import com.tourplanner.service.tourlog.TourLogOwnershipGuard;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TourLogServiceImplTest {

    @Mock private TourLogRepository tourLogRepository;
    @Mock private TourLogMapper tourLogMapper;
    @Mock private TourLogOwnershipGuard ownershipGuard;

    @InjectMocks
    private TourLogServiceImpl sut;

    private User owner;
    private Tour tour;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("user@test.com");

        tour = new Tour();
        tour.setId(10L);
        tour.setName("Vienna → Salzburg");
        tour.setFromLocation("Vienna");
        tour.setToLocation("Salzburg");
        tour.setTransportType(TransportType.CAR);
        tour.setOwner(owner);
    }

    // ── Fixture helpers ────────────────────────────────────────────────────────

    private static TourLog tourLogEntity(Long id, Tour tour) {
        TourLog log = new TourLog();
        log.setId(id);
        log.setTour(tour);
        log.setDateTime(LocalDateTime.of(2024, 6, 1, 10, 0));
        log.setComment("Schöne Wanderung");
        log.setDifficulty(2);
        log.setTotalDistance(15.5);
        log.setTotalTime(120L);
        log.setRating(4);
        return log;
    }

    private static TourLogDto dtoFor(Long id, Long tourId) {
        return new TourLogDto(
                id, tourId,
                LocalDateTime.of(2024, 6, 1, 10, 0),
                "Schöne Wanderung",
                2, 15.5, 120L, 4);
    }

    // ── findByTourId ───────────────────────────────────────────────────────────

    @Test
    void getLogsByTour_returnsList() {
        TourLog log1 = tourLogEntity(1L, tour);
        TourLog log2 = tourLogEntity(2L, tour);
        TourLogDto dto1 = dtoFor(1L, tour.getId());
        TourLogDto dto2 = dtoFor(2L, tour.getId());

        when(ownershipGuard.requireOwnedTour(tour.getId())).thenReturn(tour);
        when(tourLogRepository.findByTourId(tour.getId())).thenReturn(List.of(log1, log2));
        when(tourLogMapper.toDto(log1)).thenReturn(dto1);
        when(tourLogMapper.toDto(log2)).thenReturn(dto2);

        List<TourLogDto> result = sut.findByTourId(tour.getId());

        assertThat(result).containsExactly(dto1, dto2);
        verify(tourLogRepository).findByTourId(tour.getId());
    }

    // ── create ─────────────────────────────────────────────────────────────────

    @Test
    void createLog_validData_savesLog() {
        TourLogDto inputDto = dtoFor(null, tour.getId());
        TourLog newEntity = tourLogEntity(null, tour);
        TourLog savedEntity = tourLogEntity(5L, tour);
        TourLogDto expectedDto = dtoFor(5L, tour.getId());

        when(ownershipGuard.requireOwnedTour(tour.getId())).thenReturn(tour);
        when(tourLogMapper.toNewEntity(inputDto, tour)).thenReturn(newEntity);
        when(tourLogRepository.save(newEntity)).thenReturn(savedEntity);
        when(tourLogMapper.toDto(savedEntity)).thenReturn(expectedDto);

        TourLogDto result = sut.create(tour.getId(), inputDto);

        assertThat(result).isEqualTo(expectedDto);
        verify(tourLogRepository).save(newEntity);
    }

    @Test
    void createLog_tourNotFound_throwsException() {
        TourLogDto inputDto = dtoFor(null, 99L);

        when(ownershipGuard.requireOwnedTour(99L))
                .thenThrow(new TourNotFoundException(99L));

        assertThatThrownBy(() -> sut.create(99L, inputDto))
                .isInstanceOf(TourNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    void deleteLog_existing_removesLog() {
        TourLog log = tourLogEntity(7L, tour);

        when(ownershipGuard.requireLogOfOwnedTour(tour.getId(), 7L)).thenReturn(log);

        sut.delete(tour.getId(), 7L);

        verify(tourLogRepository).delete(log);
    }

    @Test
    void deleteLog_notFound_throwsResponseStatusException() {
        when(ownershipGuard.requireLogOfOwnedTour(tour.getId(), 42L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> sut.delete(tour.getId(), 42L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ── update ─────────────────────────────────────────────────────────────────

    @Test
    void updateLog_changesFields() {
        TourLog existingEntity = tourLogEntity(3L, tour);
        TourLogDto updateDto = new TourLogDto(
                3L, tour.getId(),
                LocalDateTime.of(2024, 7, 15, 9, 30),
                "Geänderter Kommentar",
                3, 20.0, 180L, 5);
        TourLog savedEntity = tourLogEntity(3L, tour);
        TourLogDto expectedDto = new TourLogDto(
                3L, tour.getId(),
                LocalDateTime.of(2024, 7, 15, 9, 30),
                "Geänderter Kommentar",
                3, 20.0, 180L, 5);

        when(ownershipGuard.requireLogOfOwnedTour(tour.getId(), 3L)).thenReturn(existingEntity);
        when(tourLogRepository.save(existingEntity)).thenReturn(savedEntity);
        when(tourLogMapper.toDto(savedEntity)).thenReturn(expectedDto);

        TourLogDto result = sut.update(tour.getId(), 3L, updateDto);

        assertThat(result).isEqualTo(expectedDto);
        verify(tourLogMapper).apply(updateDto, existingEntity);
        verify(tourLogRepository).save(existingEntity);
    }
}
