package com.healthlink.clinicsystem.repository;

import com.healthlink.clinicsystem.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Active doctors list
    List<Doctor> findByActiveTrue();

    long countByActiveTrue();

    // Find by email
    Doctor findByEmail(String email);

    // For dropdown or filtering
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);

    // 🔍 SEARCH SUPPORT (FULL SEARCH)
    List<Doctor> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
            String firstName,
            String lastName,
            String email,
            String specialization
    );
}
