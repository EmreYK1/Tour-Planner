// backend/src/main/java/com/tourplanner/TourPlannerApplication.java
// Einstiegspunkt der Spring-Boot-Anwendung (main).
package com.tourplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TourPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourPlannerApplication.class, args);
    }
}
