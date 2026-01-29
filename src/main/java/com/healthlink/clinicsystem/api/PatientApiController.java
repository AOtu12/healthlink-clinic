package com.healthlink.clinicsystem.api;

import com.healthlink.clinicsystem.entity.Patient;
import com.healthlink.clinicsystem.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientApiController {

    @Autowired
    private PatientRepository patientRepository;

    // GET /api/patients  -> list all patients
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // GET /api/patients/{id}  -> single patient
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/patients  -> create new patient
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient request) {
        Patient patient = new Patient();

        // Only using fields that exist in Patient.java
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setAddress(request.getAddress());

        Patient saved = patientRepository.save(patient);
        return ResponseEntity.ok(saved);
    }

    // PUT /api/patients/{id}  -> update existing
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id,
                                                 @RequestBody Patient request) {
        return patientRepository.findById(id)
                .map(existing -> {
                    // Update only known fields
                    existing.setFirstName(request.getFirstName());
                    existing.setLastName(request.getLastName());
                    existing.setEmail(request.getEmail());
                    existing.setPhone(request.getPhone());
                    existing.setDateOfBirth(request.getDateOfBirth());
                    existing.setAddress(request.getAddress());
                    Patient updated = patientRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/patients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (!patientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        patientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
