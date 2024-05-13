package com.bus.response;

import com.bus.tables.enumerations.WeekDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllRoutes {
    private UUID id;
    private UUID ownerId;
    private UUID busId;
    private String startLocation;
    private String endLocation;
    private LocalTime startTime;
    private LocalTime endTime;
    private String busName;
    private WeekDay day;
}
