package com.bus.repository;

import com.bus.tables.Route;
import com.bus.tables.enumerations.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BusRouteRepository extends JpaRepository<Route, UUID> {
    List<Route> findByBusIdAndDay(UUID busId, WeekDay day);

    List<Route> findAllByOwnerId(UUID id);

    Route findByIdAndOwnerId(UUID id, UUID id1);

    List<Route> findAllByBusId(UUID id);

    List<Route> findAllByDay(WeekDay day);
}
