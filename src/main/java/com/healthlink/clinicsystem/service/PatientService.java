package com.healthlink.clinicsystem.service;

import com.healthlink.clinicsystem.entity.Patient;
import com.healthlink.clinicsystem.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // ----------------------------- BASIC CRUD -----------------------------

    public List<Patient> getAllPatients() {
        return patientRepository.findAll(); // Fetch all patients
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id); // Find by ID
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient); // Create/update
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id); // Remove patient
    }

    // ----------------------------- SEARCH ---------------------------------

    public List<Patient> searchPatients(String searchTerm) {
        return patientRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    public List<Patient> searchPatientsByPhone(String phone) {
        return patientRepository.findByPhoneContaining(phone);
    }

    // --------------------------- DASHBOARD DATA ----------------------------

    public long getPatientCount() {
        return patientRepository.countAllPatients(); // Total count
    }

    public Patient createEmptyPatient() {
        return new Patient(); // returns a blank patient object
    }


    public long countPatients() {
        return patientRepository.countAllPatients(); // Alias
    }

    public List<Patient> getRecentPatients() {
        return patientRepository.findTop5ByOrderByIdDesc(); // Latest 5
    }

    public List<Patient> getPatientsForExport() {
        return patientRepository.findAll(); // Export use-case
    }
}
