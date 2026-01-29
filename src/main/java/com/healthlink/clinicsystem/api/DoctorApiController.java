package com.healthlink.clinicsystem.api;

import com.healthlink.clinicsystem.entity.Doctor;
import com.healthlink.clinicsystem.repository.DoctorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorApiController {

    private final DoctorRepository doctorRepository;

    public DoctorApiController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // ========================= GET ALL DOCTORS =========================
    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // ========================= GET ONE DOCTOR =========================
    @GetMapping("/{id}")
    public Doctor getDoctor(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));
    }

    // ========================= CREATE DOCTOR =========================
    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        doctor.setId(null); // ensure new insert
        return doctorRepository.save(doctor);
    }

    // ========================= UPDATE DOCTOR =========================
    @PutMapping("/{id}")
    public Doctor updateDoctor(
            @PathVariable Long id,
            @RequestBody Doctor update) {

        Doctor d = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));

        d.setFirstName(update.getFirstName());
        d.setLastName(update.getLastName());
        d.setSpecialization(update.getSpecialization());
        d.setEmail(update.getEmail());
        d.setPhone(update.getPhone());
        d.setActive(update.getActive());

        return doctorRepository.save(d);
    }

    // ========================= DELETE DOCTOR =========================
    @DeleteMapping("/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found: " + id);
        }

        doctorRepository.deleteById(id);
        return "Doctor deleted successfully.";
    }
}
