// backend/src/main/java/com/tourplanner/repository/TourRepository.java
// Spring-Data-JPA-Repository für die Entity Tour (Tabelle tours).
package com.tourplanner.repository;

import com.tourplanner.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {

    @Query("SELECT t FROM Tour t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.fromLocation) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.toLocation) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tour> searchByText(@Param("query") String query);
}
