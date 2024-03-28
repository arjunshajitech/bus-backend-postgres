package com.bus.response;

import com.bus.tables.Bus;
import com.bus.tables.Route;
import com.bus.tables.SubRoute;
import lombok.Data;

import java.util.List;

@Data
public class BusResponse {
    private Bus bus;
    private Route route;
    private List<SubRoute> subRoute;
}
