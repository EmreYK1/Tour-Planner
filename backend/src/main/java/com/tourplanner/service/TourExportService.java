package com.tourplanner.service;

import com.tourplanner.dto.TourExportDto;
import java.util.List;

public interface TourExportService {
    List<TourExportDto> exportAllTours();
}
