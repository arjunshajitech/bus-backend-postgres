package com.bus.request;

import com.bus.tables.enumerations.UserRole;
import lombok.Data;

@Data
public class UserSignup {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private UserRole role = UserRole.USER;
}
