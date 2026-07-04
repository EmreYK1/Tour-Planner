package com.tourplanner.repository;

import com.tourplanner.model.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TourLogRepository extends JpaRepository<TourLog, Long> {

    List<TourLog> findByTourId(Long tourId);

    List<TourLog> findByTourIdAndCommentContainingIgnoreCase(Long tourId, String comment);

    // Lädt alle Logs für eine Menge von Tour-IDs in einer einzigen Query; verhindert N+1.
    @Query("SELECT l FROM TourLog l WHERE l.tour.id IN :tourIds")
    List<TourLog> findAllByTourIds(@Param("tourIds") Collection<Long> tourIds);

    // Lädt alle Logs, deren Kommentar den Suchbegriff enthält, beschränkt auf eine Menge von Tour-IDs.
    @Query("SELECT l FROM TourLog l WHERE l.tour.id IN :tourIds "
            + "AND LOWER(l.comment) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TourLog> findByTourIdsAndCommentContaining(
            @Param("tourIds") Collection<Long> tourIds,
            @Param("query") String query);
}
