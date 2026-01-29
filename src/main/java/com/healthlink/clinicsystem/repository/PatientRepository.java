package com.healthlink.clinicsystem.repository;

import com.healthlink.clinicsystem.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Basic keyword search
    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    List<Patient> findByPhoneContaining(String phone);

    // Dashboard: latest patients
    List<Patient> findTop5ByOrderByIdDesc();

    // Advanced search
    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContaining(
            String firstName,
            String lastName,
            String email,
            String phone
    );

    // Quick filter by address
    List<Patient> findByAddressContainingIgnoreCase(String address);

    // 🔥 Correct patient count query for Admin dashboard
    @Query("SELECT COUNT(p) FROM Patient p")
    long countAllPatients();
}
