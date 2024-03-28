package com.bus.repository;

import com.bus.tables.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BusRepository extends JpaRepository<Bus, UUID> {
    boolean existsByRegistrationNumber(String registrationNumber);

    List<Bus> findAllByOwnerId(UUID id);

    Bus findByIdAndOwnerId(UUID id, UUID id1);

    Bus findOneById(UUID busId);
}
