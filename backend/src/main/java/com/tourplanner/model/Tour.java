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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

    /** Strecke in km */
    @Column(nullable = false)
    private double distance;

    /** Dauer in Sekunden */
    @Column(name = "estimated_time_seconds", nullable = false)
    private long estimatedTime;

    @Column(length = 2000)
    private String image;
}
