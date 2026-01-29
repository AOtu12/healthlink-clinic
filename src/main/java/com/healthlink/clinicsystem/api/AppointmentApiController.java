package com.healthlink.clinicsystem.api;

import com.healthlink.clinicsystem.dto.AppointmentRequestDto;
import com.healthlink.clinicsystem.entity.Appointment;
import com.healthlink.clinicsystem.service.AppointmentBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Appointments", description = "REST APIs for appointment booking")
public class AppointmentApiController {

    private final AppointmentBookingService bookingService;

    public AppointmentApiController(AppointmentBookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ===================== 1️⃣ Doctor availability =====================

    @GetMapping("/doctors/{doctorId}/availability")
    @Operation(
            summary = "Get doctor availability",
            description = "Returns all free 30-minute slots for a doctor on a specific date."
    )
    public ResponseEntity<List<LocalTime>> getDoctorAvailability(
            @PathVariable Long doctorId,
            @RequestParam("date") LocalDate date) {

        List<LocalTime> slots = bookingService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(slots);
    }

    // ===================== 2️⃣ Book appointment =====================

    @PostMapping("/appointments")
    @Operation(
            summary = "Book appointment",
            description = "Books an appointment for a patient with a doctor at given date and time, with conflict checking."
    )
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequestDto dto) {
        try {
            Appointment appointment = bookingService.bookAppointment(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (IllegalArgumentException e) {
            // Patient or Doctor not found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            // Conflict – doctor busy at that time
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
