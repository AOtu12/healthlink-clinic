package com.healthlink.clinicsystem.repository;

import com.healthlink.clinicsystem.entity.User;
import com.healthlink.clinicsystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username); // Lookup by username
    Optional<User> findByEmail(String email);       // Lookup by email

    List<User> findByRole(Role role);               // Filter by role
    List<User> findByIsActiveTrue();                // Only active users

    @Query("SELECT u FROM User u WHERE u.username LIKE %:search% OR u.email LIKE %:search%")
    List<User> searchUsers(@Param("search") String search); // Search by text

    boolean existsByUsername(String username); // Duplicate check
    boolean existsByEmail(String email);       // Duplicate check

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveUsersByRole(@Param("role") Role role); // Stats by role

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'PATIENT'")
    long countPatients(); // Count users with PATIENT role (if used)
}
