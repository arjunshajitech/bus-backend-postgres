package com.bus.response;

import com.bus.tables.SubRoute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusSubRoutes {
    private SubRoute subRoute;
    private boolean completed;
}
