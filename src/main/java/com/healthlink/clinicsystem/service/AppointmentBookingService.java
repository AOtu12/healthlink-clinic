package com.healthlink.clinicsystem.service;

import com.healthlink.clinicsystem.dto.AppointmentRequestDto;
import com.healthlink.clinicsystem.entity.Appointment;
import com.healthlink.clinicsystem.entity.AppointmentStatus;
import com.healthlink.clinicsystem.entity.Doctor;
import com.healthlink.clinicsystem.entity.Patient;
import com.healthlink.clinicsystem.repository.AppointmentRepository;
import com.healthlink.clinicsystem.repository.DoctorRepository;
import com.healthlink.clinicsystem.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentBookingService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentBookingService(AppointmentRepository appointmentRepository,
                                     DoctorRepository doctorRepository,
                                     PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Returns all available 30-minute slots for a doctor on a specific date,
     * between 09:00 and 17:00, excluding already booked appointments.
     */
    public List<LocalTime> getAvailableSlots(Long doctorId, LocalDate date) {
        // Define working hours 09:00 – 17:00
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);

        // Build all 30-minute slots
        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime current = start;
        while (current.isBefore(end)) {
            allSlots.add(current);
            current = current.plusMinutes(30);
        }

        // Fetch existing appointments for that doctor & date
        List<Appointment> existingAppointments =
                appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);

        // Remove times that are already booked (only SCHEDULED & COMPLETED block time)
        for (Appointment appt : existingAppointments) {
            if (appt.getStatus() == AppointmentStatus.SCHEDULED
                    || appt.getStatus() == AppointmentStatus.COMPLETED) {
                allSlots.remove(appt.getAppointmentTime());
            }
        }

        return allSlots;
    }

    /**
     * Books an appointment for a patient with a doctor at given date/time.
     * Throws IllegalStateException if doctor already has appointment at that time.
     */
    @Transactional
    public Appointment bookAppointment(AppointmentRequestDto dto) {
        // Fetch patient
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + dto.getPatientId()));

        // Fetch doctor
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + dto.getDoctorId()));

        LocalDate date = dto.getAppointmentDate();
        LocalTime time = dto.getAppointmentTime();

        // Check conflicts for this doctor on this date
        List<Appointment> existingAppointments =
                appointmentRepository.findByDoctorIdAndAppointmentDate(doctor.getId(), date);

        boolean conflict = existingAppointments.stream()
                .anyMatch(a ->
                        a.getAppointmentTime().equals(time) &&
                                (a.getStatus() == AppointmentStatus.SCHEDULED
                                        || a.getStatus() == AppointmentStatus.COMPLETED));

        if (conflict) {
            throw new IllegalStateException(
                    "Doctor already has an appointment at " + time + " on " + date);
        }

        // Create and save new appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setNotes(dto.getNotes());

        return appointmentRepository.save(appointment);
    }
}
