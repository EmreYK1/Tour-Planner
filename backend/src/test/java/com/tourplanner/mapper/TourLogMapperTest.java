package com.tourplanner.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tourplanner.dto.TourLogDto;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.model.TransportType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TourLogMapperTest {

    private TourLogMapper sut;

    @BeforeEach
    void setUp() {
        sut = new TourLogMapper();
    }

    // ── Fixture helpers ────────────────────────────────────────────────────────

    private static Tour tourWithId(Long id) {
        Tour tour = new Tour();
        tour.setId(id);
        tour.setName("Vienna → Salzburg");
        tour.setFromLocation("Vienna");
        tour.setToLocation("Salzburg");
        tour.setTransportType(TransportType.CAR);
        return tour;
    }

    private static TourLog fullTourLogEntity(Long id, Tour tour) {
        TourLog log = new TourLog();
        log.setId(id);
        log.setTour(tour);
        log.setDateTime(LocalDateTime.of(2024, 6, 15, 9, 30));
        log.setComment("Great hike!");
        log.setDifficulty(3);
        log.setTotalDistance(20.5);
        log.setTotalTime(180L);
        log.setRating(5);
        return log;
    }

    private static TourLogDto fullTourLogDto(Long id, Long tourId) {
        return new TourLogDto(
                id,
                tourId,
                LocalDateTime.of(2024, 6, 15, 9, 30),
                "Great hike!",
                3,
                20.5,
                180L,
                5
        );
    }

    // ── toNewEntity ────────────────────────────────────────────────────────────

    @Test
    void dtoToTourLog_mapsAllFields() {
        Tour tour = tourWithId(10L);
        TourLogDto dto = fullTourLogDto(null, tour.getId());

        TourLog result = sut.toNewEntity(dto, tour);

        assertThat(result.getTour()).isEqualTo(tour);
        assertThat(result.getDateTime()).isEqualTo(dto.dateTime());
        assertThat(result.getComment()).isEqualTo(dto.comment());
        assertThat(result.getDifficulty()).isEqualTo(dto.difficulty());
        assertThat(result.getTotalDistance()).isEqualTo(dto.totalDistance());
        assertThat(result.getTotalTime()).isEqualTo(dto.totalTime());
        assertThat(result.getRating()).isEqualTo(dto.rating());
    }

    // ── apply (update) ─────────────────────────────────────────────────────────

    @Test
    void apply_updatesAllMutableFields() {
        Tour tour = tourWithId(10L);
        TourLog existing = fullTourLogEntity(3L, tour);
        TourLogDto update = new TourLogDto(
                3L, tour.getId(),
                LocalDateTime.of(2024, 8, 20, 14, 0),
                "Updated comment",
                5,
                35.0,
                240L,
                3
        );

        sut.apply(update, existing);

        assertThat(existing.getDateTime()).isEqualTo(update.dateTime());
        assertThat(existing.getComment()).isEqualTo(update.comment());
        assertThat(existing.getDifficulty()).isEqualTo(update.difficulty());
        assertThat(existing.getTotalDistance()).isEqualTo(update.totalDistance());
        assertThat(existing.getTotalTime()).isEqualTo(update.totalTime());
        assertThat(existing.getRating()).isEqualTo(update.rating());
    }

    @Test
    void apply_doesNotChangeTourAssociation() {
        Tour tour = tourWithId(10L);
        TourLog existing = fullTourLogEntity(3L, tour);
        TourLogDto update = fullTourLogDto(3L, 99L);

        sut.apply(update, existing);

        assertThat(existing.getTour()).isEqualTo(tour);
    }
}
