// backend/src/main/java/com/tourplanner/repository/TourRepository.java
// Spring-Data-JPA-Repository für die Entity Tour (Tabelle tours).
package com.tourplanner.repository;

import com.tourplanner.model.Tour;
import com.tourplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findByOwner(User owner);

    Optional<Tour> findByIdAndOwner(Long id, User owner);
}
