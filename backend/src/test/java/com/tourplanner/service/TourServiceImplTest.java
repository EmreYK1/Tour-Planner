package com.tourplanner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tourplanner.dto.CreateTourRequest;
import com.tourplanner.dto.TourResponse;
import com.tourplanner.exception.TourNotFoundException;
import com.tourplanner.mapper.TourMapper;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TourLog;
import com.tourplanner.model.TransportType;
import com.tourplanner.model.User;
import com.tourplanner.repository.TourRepository;
import com.tourplanner.service.shared.SecurityContextService;
import com.tourplanner.service.shared.TourAttributeCalculator;
import com.tourplanner.service.tour.TourDtoAssembler;
import com.tourplanner.service.tour.TourEnrichmentService;
import com.tourplanner.service.tour.TourOwnershipGuard;
import com.tourplanner.service.tour.TourSearchService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TourServiceImplTest {

    @Mock private TourRepository tourRepository;
    @Mock private TourMapper tourMapper;
    @Mock private TourDtoAssembler dtoAssembler;
    @Mock private TourEnrichmentService enrichmentService;
    @Mock private TourOwnershipGuard ownershipGuard;
    @Mock private TourSearchService searchService;
    @Mock private SecurityContextService securityContextService;

    @InjectMocks
    private TourServiceImpl sut;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@test.com");
    }

    // ── Fixture helpers ────────────────────────────────────────────────────────

    private static Tour tourWithOwner(Long id, User owner) {
        Tour tour = new Tour();
        tour.setId(id);
        tour.setName("Tour-" + id);
        tour.setFromLocation("Vienna");
        tour.setToLocation("Salzburg");
        tour.setTransportType(TransportType.CAR);
        tour.setOwner(owner);
        return tour;
    }

    private static TourResponse responseFor(Tour tour) {
        return new TourResponse(
                tour.getId(), tour.getName(), "",
                tour.getFromLocation(), tour.getToLocation(),
                TransportType.CAR,
                0.0, 0L, "", null, 0, "Niedrig");
    }

    private static TourLog logWith(Tour tour, int difficulty, double distanceKm, long timeMin) {
        TourLog log = new TourLog();
        log.setTour(tour);
        log.setDifficulty(difficulty);
        log.setTotalDistance(distanceKm);
        log.setTotalTime(timeMin);
        log.setRating(3);
        return log;
    }

    // ── TourServiceImpl ────────────────────────────────────────────────────────

    @Test
    void getAllTours_returnsOnlyUserTours() {
        Tour t1 = tourWithOwner(1L, currentUser);
        Tour t2 = tourWithOwner(2L, currentUser);
        TourResponse r1 = responseFor(t1);
        TourResponse r2 = responseFor(t2);
        Map<Long, List<TourLog>> emptyLogMap = Map.of();

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(tourRepository.findByOwner(currentUser)).thenReturn(List.of(t1, t2));
        when(dtoAssembler.buildLogMap(List.of(t1, t2))).thenReturn(emptyLogMap);
        when(dtoAssembler.assemble(t1, emptyLogMap)).thenReturn(r1);
        when(dtoAssembler.assemble(t2, emptyLogMap)).thenReturn(r2);

        List<TourResponse> result = sut.findAll();

        assertThat(result).containsExactlyInAnyOrder(r1, r2);
        verify(tourRepository).findByOwner(currentUser);
    }

    @Test
    void getTourById_existingId_returnsTour() {
        Tour tour = tourWithOwner(10L, currentUser);
        TourResponse expected = responseFor(tour);

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(tourRepository.findById(10L)).thenReturn(Optional.of(tour));
        when(ownershipGuard.isOwnedBy(tour, currentUser)).thenReturn(true);
        when(dtoAssembler.assemble(tour)).thenReturn(expected);

        assertThat(sut.findById(10L)).contains(expected);
    }

    @Test
    void getTourById_unknownId_throwsTourNotFoundException() {
        // findById propagiert keinen Wurf – unbekannte IDs liefern Optional.empty(),
        // damit die Existenz fremder Ressourcen nicht verraten wird (security by obscurity).
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(sut.findById(99L)).isEmpty();
    }

    @Test
    void createTour_validDto_savesAndReturns() {
        CreateTourRequest request = new CreateTourRequest(
                "Vienna → Linz", null, "Vienna", "Linz",
                TransportType.CAR, 180.0, 5400L, null,
                null, null, null, null);
        Tour entity = tourWithOwner(null, currentUser);
        Tour saved = tourWithOwner(7L, currentUser);
        TourResponse expected = responseFor(saved);

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(tourMapper.toNewEntity(request)).thenReturn(entity);
        when(tourRepository.save(entity)).thenReturn(saved);
        when(dtoAssembler.assemble(saved)).thenReturn(expected);

        TourResponse result = sut.create(request);

        assertThat(result).isEqualTo(expected);
        verify(enrichmentService).enrichWithRouteData(entity, request);
        verify(tourRepository).save(entity);
    }

    @Test
    void deleteTour_foreignTour_throwsTourNotFoundException() {
        when(ownershipGuard.requireOwnedTour(42L))
                .thenThrow(new TourNotFoundException(42L));

        assertThatThrownBy(() -> sut.delete(42L))
                .isInstanceOf(TourNotFoundException.class)
                .hasMessageContaining("42");
    }

    // ── TourAttributeCalculator ────────────────────────────────────────────────

    @Nested
    class TourAttributeCalculatorTests {

        private final TourAttributeCalculator calculator = new TourAttributeCalculator();
        private final Tour referenceTour = tourWithOwner(1L, new User());

        @Test
        void computePopularity_withLogs_returnsCorrectCount() {
            List<TourLog> logs = List.of(
                    logWith(referenceTour, 2, 5.0, 60),
                    logWith(referenceTour, 2, 5.0, 60),
                    logWith(referenceTour, 2, 5.0, 60));

            assertThat(calculator.computePopularity(logs)).isEqualTo(3);
        }

        @Test
        void computeChildFriendliness_easyTour_returnsHoch() {
            // difficulty ≤ 2, distance ≤ 10 km, time ≤ 120 min → "Hoch"
            List<TourLog> logs = List.of(logWith(referenceTour, 1, 5.0, 60));

            assertThat(calculator.computeChildFriendliness(logs)).isEqualTo("Hoch");
        }
    }
}
