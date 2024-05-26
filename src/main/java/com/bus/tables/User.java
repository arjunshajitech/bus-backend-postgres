package com.bus.tables;

import com.bus.tables.enumerations.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_details")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private UUID cookie;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.DEFAULT;

    public User(String firstName, String lastName,
                String phoneNumber, String email,
                String password, UserRole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phoneNumber;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(UUID id, String firstName, String lastName, String phone, String email, String password, UserRole role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
