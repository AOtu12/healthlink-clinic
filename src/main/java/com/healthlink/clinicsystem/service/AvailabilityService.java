package com.healthlink.clinicsystem.service;

import com.healthlink.clinicsystem.entity.Appointment;
import com.healthlink.clinicsystem.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityService {

    private final AppointmentRepository appointmentRepository;

    public AvailabilityService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // Fixed clinic time slots: 9 AM – 4 PM (hourly)
    private final LocalTime[] TIME_SLOTS = {
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(12, 0),
            LocalTime.of(13, 0),
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            LocalTime.of(16, 0)
    };

    public List<LocalTime> getDoctorAvailability(Long doctorId, LocalDate date) {

        // All booked appointments for doctor on selected date
        List<Appointment> booked =
                appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);

        List<LocalTime> available = new ArrayList<>();

        // Check each time slot for availability
        for (LocalTime slot : TIME_SLOTS) {
            boolean taken =
                    booked.stream().anyMatch(a -> a.getAppointmentTime().equals(slot));

            if (!taken) {
                available.add(slot); // Add only free slots
            }
        }

        return available;
    }
}
