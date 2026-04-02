// backend/src/main/java/com/tourplanner/model/Tour.java
// JPA-Entity für eine Tour; mappt auf die Tabelle tours.
package com.tourplanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tours")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "from_place", nullable = false, length = 500)
    private String fromLocation;

    @Column(name = "to_place", nullable = false, length = 500)
    private String toLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type", nullable = false, length = 50)
    private TransportType transportType;

    // Strecke in km, im JSON-Feld "distance"
    @Column(nullable = false)
    private double distance;

    // Dauer in Sekunden, im JSON-Feld "estimatedTime"
    @Column(name = "estimated_time_seconds", nullable = false)
    private long estimatedTime;

    @Column(length = 2000)
    private String image;

    public Tour() {
        // leerer Konstruktor nur für JPA
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
