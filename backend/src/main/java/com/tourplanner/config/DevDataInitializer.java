// backend/src/main/java/com/tourplanner/config/DevDataInitializer.java
// Legt im Profil „dev“ Demo-Touren an, wenn die Datenbank leer ist.
package com.tourplanner.config;

import com.tourplanner.model.Tour;
import com.tourplanner.model.TransportType;
import com.tourplanner.repository.TourRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataInitializer implements ApplicationRunner {

    private final TourRepository tourRepository;

    public DevDataInitializer(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (tourRepository.count() > 0) {
            return;
        }
        Tour tour = new Tour();
        tour.setName("Wienerwald Tour");
        tour.setDescription(
                "Rundtour durch den Wienerwald mit Aussichtspunkten und ruhigen Waldwegen.");
        tour.setFromLocation("Wien Hietzing");
        tour.setToLocation("Kaltenleutgeben");
        tour.setTransportType(TransportType.BICYCLE);
        tour.setDistance(35.2);
        tour.setEstimatedTime(14_400L);
        tour.setImage("/assets/tours/wienerwald.jpg");
        tourRepository.save(tour);
    }
}
