package com.bus.request;

import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class CreateSubRoute {
    private UUID routeId;
    private String location;
    private LocalTime busTime;
}
