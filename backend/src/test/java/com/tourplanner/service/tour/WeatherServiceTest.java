package com.tourplanner.service.tour;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tourplanner.client.WeatherClient;
import com.tourplanner.dto.WeatherDto;
import com.tourplanner.exception.WeatherApiException;
import com.tourplanner.model.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock private TourOwnershipGuard ownershipGuard;
    @Mock private WeatherClient weatherClient;

    @InjectMocks
    private WeatherService sut;

    private Tour tour;

    @BeforeEach
    void setUp() {
        tour = new Tour();
        tour.setId(1L);
    }

    @Test
    void getWeatherForTour_withCoordinates_returnsWeatherFromClient() {
        tour.setFromLat(48.2082);
        tour.setFromLon(16.3738);
        WeatherDto expected = new WeatherDto("Wien", 18.0, 17.5, "leichter Regen", "10d", 3.2, 65);

        when(ownershipGuard.requireOwnedTour(1L)).thenReturn(tour);
        when(weatherClient.fetchCurrentWeather(48.2082, 16.3738)).thenReturn(expected);

        WeatherDto result = sut.getWeatherForTour(1L);

        assertThat(result).isEqualTo(expected);
        verify(weatherClient).fetchCurrentWeather(48.2082, 16.3738);
    }

    @Test
    void getWeatherForTour_withoutCoordinates_throwsWeatherApiException() {
        // Weder fromLat noch fromLon gesetzt (z. B. importierte oder alte Tour)
        when(ownershipGuard.requireOwnedTour(1L)).thenReturn(tour);

        assertThatThrownBy(() -> sut.getWeatherForTour(1L))
                .isInstanceOf(WeatherApiException.class)
                .hasMessageContaining("Koordinaten");
    }

    @Test
    void getWeatherForTour_foreignTour_propagatesOwnershipGuardException() {
        when(ownershipGuard.requireOwnedTour(99L))
                .thenThrow(new com.tourplanner.exception.TourNotFoundException(99L));

        assertThatThrownBy(() -> sut.getWeatherForTour(99L))
                .isInstanceOf(com.tourplanner.exception.TourNotFoundException.class);
    }
}
