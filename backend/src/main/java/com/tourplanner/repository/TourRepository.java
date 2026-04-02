// backend/src/main/java/com/tourplanner/repository/TourRepository.java
// Spring-Data-JPA-Repository für die Entity Tour (Tabelle tours).
package com.tourplanner.repository;

import com.tourplanner.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourRepository extends JpaRepository<Tour, Long> {
}
