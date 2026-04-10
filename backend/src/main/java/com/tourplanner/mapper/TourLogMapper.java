package com.tourplanner.mapper;

import com.tourplanner.model.Tour;
import com.tourplanner.dto.TourLogDto;
import com.tourplanner.model.TourLog;
import org.springframework.stereotype.Component;

@Component
public class TourLogMapper {

    public TourLogDto toDto(TourLog entity) {
        return new TourLogDto(
            entity.getId(),
            entity.getTour().getId(),
            entity.getDateTime(),
            entity.getComment(),
            entity.getDifficulty(),
            entity.getTotalDistance(),
            entity.getTotalTime(),
            entity.getRating()
        );
    }

    public TourLog toNewEntity(TourLogDto dto, Tour tour) {
        TourLog tourLog = new TourLog();
        apply(dto, tourLog);
        tourLog.setTour(tour);
        return tourLog;
    }

    public void apply(TourLogDto dto, TourLog entity) {
        entity.setDateTime(dto.dateTime());
        entity.setComment(dto.comment());
        entity.setDifficulty(dto.difficulty());
        entity.setTotalDistance(dto.totalDistance());
        entity.setTotalTime(dto.totalTime());
        entity.setRating(dto.rating());
    }
    
}
