package com.bus.request;

import com.bus.tables.enumerations.UserRole;
import lombok.Data;

@Data
public class CreateBusOwner {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private UserRole role = UserRole.BUS_OWNER;
}
