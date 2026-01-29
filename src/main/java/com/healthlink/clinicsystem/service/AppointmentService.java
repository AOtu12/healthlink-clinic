package com.healthlink.clinicsystem.service;

import com.healthlink.clinicsystem.entity.Appointment;
import com.healthlink.clinicsystem.entity.AppointmentStatus;
import com.healthlink.clinicsystem.entity.Doctor;
import com.healthlink.clinicsystem.repository.AppointmentRepository;
import com.healthlink.clinicsystem.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;   // Inject appointment repository

    @Autowired
    private DoctorRepository doctorRepository;             // Inject doctor repository

    // -------------------------------------------------------
    // BASIC CRUD
    // -------------------------------------------------------

    // OLD METHOD (used by existing dashboards)
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null); // Return appointment or null
    }

    // NEW METHOD FOR ADMIN CONTROLLERS
    public Optional<Appointment> getAppointmentOptionalById(Long id) {
        return appointmentRepository.findById(id); // Return optional for better handling
    }

    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment); // Create or update appointment
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id); // Delete appointment by ID
    }

    // -------------------------------------------------------
    // FILTERS
    // -------------------------------------------------------

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date); // Filter by date
    }

    public List<Appointment> getAppointmentsByDoctorAndDate(Doctor doctor, LocalDate date) {
        return appointmentRepository.findByDoctorAndAppointmentDate(doctor, date); // Filter by doctor + date
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status); // Filter by status
    }

    public List<Appointment> searchAppointments(String keyword) {
        return appointmentRepository.searchAppointments(keyword); // Search keyword in patient/doctor fields
    }

    // -------------------------------------------------------
    // DASHBOARD STATS
    // -------------------------------------------------------

    public long countToday() {
        return appointmentRepository.countByAppointmentDate(LocalDate.now()); // Today's total appointments
    }

    public long countCancelledToday() {
        return appointmentRepository.countByAppointmentDateAndStatus(LocalDate.now(), AppointmentStatus.CANCELLED);
    }

    public long countAll() {
        return appointmentRepository.count(); // All appointments total
    }

    public long countCancelledAll() {
        return appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);
    }

    // OLD METHOD FOR RECEPTIONIST DASHBOARD
    public List<Appointment> getTodaysAppointments() {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByAppointmentDate(today); // Return today's appointments
    }

    // -------------------------------------------------------
    // ADMIN SEARCH (EMAIL + DATE + STATUS)
    // -------------------------------------------------------

    public List<Appointment> searchAdminByEmail(String doctorEmail,
                                                LocalDate date,
                                                AppointmentStatus status) {

        // Determine which filters are active
        boolean filterDoctor = doctorEmail != null && !doctorEmail.isBlank();
        boolean filterDate   = date != null;
        boolean filterStatus = status != null;

        Doctor doctor = null;
        if (filterDoctor) {
            doctor = doctorRepository.findByEmail(doctorEmail); // Fetch doctor by email if provided
        }

        // No filters → return all
        if (!filterDoctor && !filterDate && !filterStatus) {
            return appointmentRepository.findAll();
        }

        // Only doctor filter
        if (filterDoctor && !filterDate && !filterStatus) {
            Doctor finalDoc = doctor;
            return appointmentRepository.findAll().stream()
                    .filter(a -> finalDoc != null && a.getDoctor().getEmail().equals(finalDoc.getEmail()))
                    .toList();
        }

        // Only date filter
        if (!filterDoctor && filterDate && !filterStatus) {
            return appointmentRepository.findByAppointmentDate(date);
        }

        // Only status filter
        if (!filterDoctor && !filterDate && filterStatus) {
            return appointmentRepository.findByStatus(status);
        }

        // Doctor + date
        if (filterDoctor && filterDate && !filterStatus) {
            Doctor finalDoc = doctor;
            return appointmentRepository.findByAppointmentDate(date).stream()
                    .filter(a -> finalDoc != null && a.getDoctor().getEmail().equals(finalDoc.getEmail()))
                    .toList();
        }

        // Doctor + status
        if (filterDoctor && !filterDate && filterStatus) {
            Doctor finalDoc = doctor;
            return appointmentRepository.findByStatus(status).stream()
                    .filter(a -> finalDoc != null && a.getDoctor().getEmail().equals(finalDoc.getEmail()))
                    .toList();
        }

        // Date + status
        if (!filterDoctor && filterDate && filterStatus) {
            return appointmentRepository.findByAppointmentDate(date).stream()
                    .filter(a -> a.getStatus() == status)
                    .toList();
        }

        // All filters active
        Doctor finalDoc = doctor;
        return appointmentRepository.findByAppointmentDate(date).stream()
                .filter(a -> finalDoc != null && a.getDoctor().getEmail().equals(finalDoc.getEmail()))
                .filter(a -> a.getStatus() == status)
                .toList();
    }

    // -------------------------------------------------------
    // REPORT RANGE
    // -------------------------------------------------------

    public List<Appointment> findBetweenDates(LocalDate start, LocalDate end) {
        // Filter manually between date range
        return appointmentRepository.findAll().stream()
                .filter(a -> !a.getAppointmentDate().isBefore(start))
                .filter(a -> !a.getAppointmentDate().isAfter(end))
                .toList();
    }

    // -------------------------------------------------------
    // BACKWARD COMPATIBILITY (OLD DASHBOARDS)
    // -------------------------------------------------------

    public long getTodaysAppointmentCount() {
        return countToday(); // Legacy method mapping
    }

    public List<Appointment> getAppointmentsForDoctorOn(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndAppointmentDateOrderByAppointmentTime(doctorId, date);
    }

    public List<Appointment> getUpcomingAppointmentsForDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorIdAndAppointmentDateBetweenOrderByAppointmentDateAscAppointmentTimeAsc(
                doctorId,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(7)
        );
    }

    public long countUniquePatientsForDoctor(Long doctorId) {
        return appointmentRepository.countDistinctPatientsByDoctorId(doctorId);
    }

    // BACKWARD COMPATIBILITY: old controllers used 2 params
    public List<Appointment> getUpcomingAppointmentsForDoctor(Long doctorId, int days) {
        return getUpcomingAppointmentsForDoctor(doctorId); // Ignore "days", keep old behavior
    }

    public Appointment rescheduleAppointment(Long id, LocalDate newDate, LocalTime newTime) {

        // Find appointment or fail
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + id));

        // Check for scheduling conflicts for this doctor
        boolean conflict = appointmentRepository
                .findByDoctorIdAndAppointmentDate(appt.getDoctor().getId(), newDate)
                .stream()
                .anyMatch(a -> a.getAppointmentTime().equals(newTime)
                        && !a.getId().equals(id)
                        && (a.getStatus() == AppointmentStatus.SCHEDULED ||
                        a.getStatus() == AppointmentStatus.COMPLETED));

        if (conflict) {
            // Prevent double-booking
            throw new IllegalStateException(
                    "Doctor already has an appointment at " + newTime + " on " + newDate
            );
        }

        // Apply updates
        appt.setAppointmentDate(newDate);
        appt.setAppointmentTime(newTime);

        return appointmentRepository.save(appt); // Save changes
    }

}
