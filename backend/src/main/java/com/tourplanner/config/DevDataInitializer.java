// backend/src/main/java/com/tourplanner/config/DevDataInitializer.java
// Legt im Profil „dev“ Demo-Daten an: Testuser und ggf. Demo-Tour.
package com.tourplanner.config;

import com.tourplanner.model.Tour;
import com.tourplanner.model.TransportType;
import com.tourplanner.model.User;
import com.tourplanner.repository.TourRepository;
import com.tourplanner.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataInitializer implements ApplicationRunner {

    private static final String TEST_USER_EMAIL = "test@example.com";

    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public DevDataInitializer(TourRepository tourRepository, UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        User testUser = ensureTestUser();
        assignOwnerToOrphanTours(testUser);

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
        tour.setOwner(testUser);
        tourRepository.save(tour);
    }

    private User ensureTestUser() {
        return userRepository
                .findByEmail(TEST_USER_EMAIL)
                .orElseGet(
                        () -> {
                            User user = new User();
                            user.setEmail(TEST_USER_EMAIL);
                            // Platzhalter bis Auth-Task (Klartext: secret123)
                            user.setPasswordHash("secret123");
                            user.setCreatedAt(LocalDateTime.now());
                            return userRepository.save(user);
                        });
    }

    private void assignOwnerToOrphanTours(User owner) {
        tourRepository.findAll().stream()
                .filter(tour -> tour.getOwner() == null)
                .forEach(
                        tour -> {
                            tour.setOwner(owner);
                            tourRepository.save(tour);
                        });
    }
}
