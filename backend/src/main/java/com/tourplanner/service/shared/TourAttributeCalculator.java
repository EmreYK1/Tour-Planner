package com.tourplanner.service.shared;

import com.tourplanner.model.TourLog;
import java.util.List;
import org.springframework.stereotype.Component;

// Berechnet Popularity (Anzahl Logs) und Kinderfreundlichkeit (Hoch/Mittel/Niedrig) aus Tour-Logs.
@Component
public class TourAttributeCalculator {

    private static final double MAX_DIFFICULTY_HIGH = 2.0;
    private static final double MAX_DISTANCE_HIGH_KM = 10.0;
    private static final double MAX_TIME_HIGH_MIN = 120.0;

    private static final double MAX_DIFFICULTY_MEDIUM = 3.0;
    private static final double MAX_DISTANCE_MEDIUM_KM = 25.0;

    // Popularity = Anzahl der Logs
    public int computePopularity(List<TourLog> logs) {
        return logs.size();
    }

    // Hoch: difficulty≤2, distance≤10km, time≤120min | Mittel: difficulty≤3, distance≤25km | sonst Niedrig
    public String computeChildFriendliness(List<TourLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return "Niedrig";
        }

        double avgDifficulty = logs.stream().mapToInt(TourLog::getDifficulty).average().orElse(0.0);
        double avgDistanceKm = logs.stream().mapToDouble(TourLog::getTotalDistance).average().orElse(0.0);
        double avgTimeMin    = logs.stream().mapToLong(TourLog::getTotalTime).average().orElse(0.0);

        if (avgDifficulty <= MAX_DIFFICULTY_HIGH && avgDistanceKm <= MAX_DISTANCE_HIGH_KM && avgTimeMin <= MAX_TIME_HIGH_MIN) {
            return "Hoch";
        }
        if (avgDifficulty <= MAX_DIFFICULTY_MEDIUM && avgDistanceKm <= MAX_DISTANCE_MEDIUM_KM) {
            return "Mittel";
        }
        return "Niedrig";
    }
}
