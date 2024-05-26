package com.bus.tables;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "bus_details")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID ownerId;
    private String ownerName;
    private String busName;
    private String registrationNumber;

    public Bus(UUID ownerId, String ownerName,
               String busName, String registrationNumber) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.busName = busName;
        this.registrationNumber = registrationNumber;
    }
}
