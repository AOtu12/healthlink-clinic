package com.healthlink.clinicsystem.service;

import com.healthlink.clinicsystem.entity.Doctor;
import com.healthlink.clinicsystem.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    // ----------------------------- BASIC CRUD -----------------------------

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll(); // All doctors
    }

    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findByActiveTrue(); // Only active doctors
    }

    public Doctor createEmptyDoctor() {
        return new Doctor(); // returns a blank doctor object
    }


    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor); // Save doctor
    }

    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor); // Duplicate alias
    }

    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id); // Remove doctor
    }

    // ----------------------------- SEARCH ---------------------------------

    public List<Doctor> searchDoctors(String keyword) {
        return doctorRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
                        keyword, keyword, keyword, keyword
                ); // Search across fields
    }

    public Doctor findByEmail(String email) {
        return doctorRepository.findByEmail(email); // Lookup by email
    }

    // ------------------------ DASHBOARD METRICS ----------------------------

    public long getActiveDoctorCount() {
        return doctorRepository.countByActiveTrue();
    }

    public long getTotalDoctorCount() {
        return doctorRepository.count();
    }

    public long countDoctors() {
        return doctorRepository.count(); // Admin dashboard count
    }

    public Doctor getById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));
    }

    // ----------------------------- UPDATE / TOGGLE -------------------------

    @Transactional
    public void updateDoctor(Long id, Doctor updated) {
        Doctor d = getById(id);

        d.setFirstName(updated.getFirstName());
        d.setLastName(updated.getLastName());
        d.setSpecialization(updated.getSpecialization());
        d.setEmail(updated.getEmail());
        d.setActive(updated.getActive());

        doctorRepository.save(d); // Save changes
    }

    @Transactional
    public void toggleActive(Long id) {
        Doctor d = getById(id);
        Boolean curr = d.getActive();
        d.setActive(curr == null ? Boolean.FALSE : !curr); // Flip status
        doctorRepository.save(d);
    }
}
