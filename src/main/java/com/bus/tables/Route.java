package com.bus.tables;

import com.bus.tables.enumerations.UserRole;
import com.bus.tables.enumerations.WeekDay;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "bus_route_details")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID ownerId;
    private UUID busId;
    private String startLocation;
    private String endLocation;
    private LocalTime startTime;
    private LocalTime endTime;
    @Enumerated(EnumType.STRING)
    private WeekDay day = WeekDay.DEFAULT;

    public Route(UUID ownerId, UUID busId, String startLocation, String endLocation, LocalTime startTime, LocalTime endTime, WeekDay day) {
        this.ownerId = ownerId;
        this.busId = busId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
    }
}
