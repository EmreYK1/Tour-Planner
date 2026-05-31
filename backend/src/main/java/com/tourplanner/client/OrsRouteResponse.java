package com.tourplanner.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrsRouteResponse(List<Route> routes) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Route(Summary summary, Geometry geometry) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Summary(double distance, double duration) {}
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Geometry(String type, List<List<Double>> coordinates) {}
    }
}