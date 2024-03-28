package com.bus.request;

import com.bus.tables.enumerations.WeekDay;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class CreateBusRoute {

    private UUID busId;
    private String startLocation;
    private String endLocation;
    private LocalTime startTime;
    private LocalTime endTime;
    private WeekDay day = WeekDay.DEFAULT;
}
