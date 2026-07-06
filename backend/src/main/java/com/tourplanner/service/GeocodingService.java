package com.tourplanner.service;

import com.tourplanner.client.OrsClient;
import com.tourplanner.dto.GeocodingResultDto;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    private final OrsClient orsClient;

    public GeocodingService(OrsClient orsClient) {
        this.orsClient = orsClient;
    }

    public GeocodingResultDto geocode(String query) {
        return orsClient.fetchGeocode(query);
    }
}
