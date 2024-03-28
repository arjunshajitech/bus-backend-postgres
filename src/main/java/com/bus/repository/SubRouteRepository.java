package com.bus.repository;

import com.bus.tables.SubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubRouteRepository extends JpaRepository<SubRoute, UUID> {
    List<SubRoute> findAllByRouteId(UUID routeId);

    List<SubRoute> findAllByOwnerId(UUID id);

    List<SubRoute> findAllByBusId(UUID id);

    SubRoute findByIdAndOwnerId(UUID id, UUID id1);
}
