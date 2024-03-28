package com.bus.repository;

import com.bus.tables.User;
import com.bus.tables.enumerations.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailAndRole(String email, UserRole role);

    boolean existsByPhoneAndRole(String phone, UserRole role);

    User findByEmailAndRole(String emailOrPhone, UserRole userRole);

    User findByPhoneAndRole(String emailOrPhone, UserRole userRole);

    User findByCookie(UUID cookie);

    List<User> findAllByRole(UserRole userRole);

    User findByIdAndRole(UUID id, UserRole userRole);
}
