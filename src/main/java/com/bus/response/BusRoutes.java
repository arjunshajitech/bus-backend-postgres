package com.bus.response;

import com.bus.tables.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusRoutes {
    private Route route;
    private String busName;
    private String ownerName;
}
