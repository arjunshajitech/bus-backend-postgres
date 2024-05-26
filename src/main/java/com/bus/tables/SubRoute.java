package com.bus.tables;

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
@Table(name = "bus_sub_route_details")
public class SubRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID busId;
    private UUID ownerId;
    private UUID routeId;
    private String location;
    private LocalTime busTime;

    public SubRoute(UUID busId, UUID ownerId, UUID routeId,
                    String location, LocalTime busTime) {
        this.busId = busId;
        this.ownerId = ownerId;
        this.routeId = routeId;
        this.location = location;
        this.busTime = busTime;
    }
}
