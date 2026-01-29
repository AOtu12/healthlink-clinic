package com.healthlink.clinicsystem.repository;

import com.healthlink.clinicsystem.entity.Appointment;
import com.healthlink.clinicsystem.entity.AppointmentStatus;
import com.healthlink.clinicsystem.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Filter by doctor and date
    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDate date);

    // Filter by date
    List<Appointment> findByAppointmentDate(LocalDate date);

    // Filter by status
    List<Appointment> findByStatus(AppointmentStatus status);

    // Dashboard methods
    long countByAppointmentDate(LocalDate date);

    long countByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);

    // NEW: count by status (all-time)
    long countByStatus(AppointmentStatus status);

    // NEW: between dates (for reports)
    List<Appointment> findByAppointmentDateBetween(LocalDate start, LocalDate end);

    // Retrieve appointments of the day (ordered)
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date ORDER BY a.appointmentTime ASC")
    List<Appointment> findByAppointmentDateOrderByAppointmentTimeAsc(@Param("date") LocalDate date);

    // Universal Search (patient, doctor, specialization, or date)
    @Query("""
    SELECT a FROM Appointment a
    WHERE LOWER(a.patient.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(a.patient.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(a.doctor.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(a.doctor.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(a.doctor.specialization) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR CONCAT('', a.appointmentDate) LIKE CONCAT('%', :keyword, '%')
    """)
    List<Appointment> searchAppointments(@Param("keyword") String keyword);

    // Doctor-specific
    List<Appointment> findByDoctorIdAndAppointmentDateOrderByAppointmentTime(Long doctorId, LocalDate date);

    List<Appointment> findByDoctorIdAndAppointmentDateBetweenOrderByAppointmentDateAscAppointmentTimeAsc(
            Long doctorId, LocalDate start, LocalDate end);

    @Query("""
            select count(distinct a.patient.id)
            from Appointment a
            where a.doctor.id = :doctorId
           """)
    long countDistinctPatientsByDoctorId(@Param("doctorId") Long doctorId);

    long countDistinctPatientIdByDoctorId(Long doctorId);

    // NEW: Admin filter search (doctor/date/status)
    @Query("""
        SELECT a FROM Appointment a
        WHERE (:doctorId IS NULL OR a.doctor.id = :doctorId)
          AND (:date IS NULL OR a.appointmentDate = :date)
          AND (:status IS NULL OR a.status = :status)
    """)
    List<Appointment> searchAdmin(@Param("doctorId") Long doctorId,
                                  @Param("date") LocalDate date,
                                  @Param("status") AppointmentStatus status);


    // 🔹 ADD THIS (if you don't already have something similar)
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    


}
