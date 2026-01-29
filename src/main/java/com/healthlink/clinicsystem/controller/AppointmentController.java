package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.Appointment;
import com.healthlink.clinicsystem.entity.AppointmentStatus;
import com.healthlink.clinicsystem.service.AppointmentService;
import com.healthlink.clinicsystem.service.AvailabilityService;
import com.healthlink.clinicsystem.service.DoctorService;
import com.healthlink.clinicsystem.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired private AppointmentService appointmentService;
    @Autowired private DoctorService doctorService;
    @Autowired private PatientService patientService;
    @Autowired private AvailabilityService availabilityService;

    // LIST
    @GetMapping
    public String listAppointments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        List<Appointment> appointments;

        if (keyword != null && !keyword.trim().isEmpty()) {
            appointments = appointmentService.searchAppointments(keyword);
        } else if (date != null) {
            appointments = appointmentService.getAppointmentsByDate(date);
        } else {
            appointments = appointmentService.getTodaysAppointments();
        }

        appointments.forEach(a -> {
            if (a.getStatus() == null) a.setStatus(AppointmentStatus.SCHEDULED);
        });

        model.addAttribute("appointments", appointments);
        return "appointments/list";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String createAppointmentForm(Model model) {
        Appointment appt = new Appointment();

        model.addAttribute("appointment", appt);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAllDoctors());

        return "appointments/form";  // YOUR FILE
    }

    // EDIT FORM
    @GetMapping("/edit/{id}")
    public String editAppointment(@PathVariable Long id, Model model) {

        Appointment appt = appointmentService.getAppointmentById(id);

        model.addAttribute("appointment", appt);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAllDoctors());

        return "appointments/form"; // SAME FORM
    }

    // DELETE APPOINTMENT
    @PostMapping("/{id}/delete")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes ra) {

        appointmentService.deleteAppointment(id);

        ra.addFlashAttribute("success", "Appointment deleted successfully!");

        return "redirect:/appointments";
    }


    // SAVE (CREATE OR UPDATE)
    @PostMapping
    public String saveAppointment(@ModelAttribute Appointment appointment,
                                  RedirectAttributes ra) {

        appointmentService.saveAppointment(appointment);
        ra.addFlashAttribute("success", "Appointment saved successfully!");

        return "redirect:/appointments";
    }

    // AVAILABILITY PAGE (GET)
    @GetMapping("/availability")
    public String showAvailabilityForm(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "availability/availability-form";
    }

    // AVAILABILITY (POST)
    @PostMapping("/availability")
    public String checkAvailability(
            @RequestParam Long doctorId,
            @RequestParam String date,
            Model model) {

        LocalDate selectedDate = LocalDate.parse(date);

        List<LocalTime> slots = availabilityService.getDoctorAvailability(doctorId, selectedDate);

        model.addAttribute("doctorId", doctorId);
        model.addAttribute("date", selectedDate);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("availableSlots", slots);

        return "availability/availability-form";
    }

    // RESCHEDULE PAGE
    @GetMapping("/reschedule/{id}")
    public String showRescheduleForm(@PathVariable Long id, Model model) {
        Appointment appt = appointmentService.getAppointmentById(id);
        model.addAttribute("appointment", appt);
        return "appointments/reschedule";
    }

    // RESCHEDULE SAVE
    @PostMapping("/reschedule/{id}")
    public String rescheduleSave(
            @PathVariable Long id,
            @RequestParam String date,
            @RequestParam String time,
            RedirectAttributes ra) {

        Appointment appt = appointmentService.getAppointmentById(id);
        appt.setAppointmentDate(LocalDate.parse(date));
        appt.setAppointmentTime(LocalTime.parse(time));

        appointmentService.saveAppointment(appt);

        ra.addFlashAttribute("success", "Appointment rescheduled successfully!");
        return "redirect:/appointments";
    }
}
