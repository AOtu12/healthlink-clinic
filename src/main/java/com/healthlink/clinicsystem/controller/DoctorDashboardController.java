package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.Appointment;
import com.healthlink.clinicsystem.entity.AppointmentStatus;
import com.healthlink.clinicsystem.entity.Doctor;
import com.healthlink.clinicsystem.service.AppointmentService;
import com.healthlink.clinicsystem.service.DoctorService;
import com.healthlink.clinicsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorDashboardController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String doctorDashboard(Model model) {

        Doctor currentDoctor = getCurrentDoctor(); // Logged-in doctor
        if (currentDoctor == null) return "redirect:/login?error=doctor_not_found";

        LocalDate today = LocalDate.now();

        List<Appointment> todayAppointments =
                appointmentService.getAppointmentsForDoctorOn(currentDoctor.getId(), today); // Today's appointments

        List<Appointment> upcomingAppointments =
                appointmentService.getUpcomingAppointmentsForDoctor(currentDoctor.getId(), 7); // Next 7 days

        long todayCount = todayAppointments.size();
        long upcomingCount = upcomingAppointments.size();
        long patientsCount = appointmentService.countUniquePatientsForDoctor(currentDoctor.getId());

        model.addAttribute("doctor", currentDoctor);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("upcomingCount", upcomingCount);
        model.addAttribute("patientsCount", patientsCount);
        model.addAttribute("appointmentStatuses", AppointmentStatus.values());
        model.addAttribute("todayDate", today);

        return "doctors/dashboard";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status,
            RedirectAttributes redirectAttributes) {

        Doctor currentDoctor = getCurrentDoctor();

        try {
            Appointment appointment = appointmentService.getAppointmentById(id); // Load appointment
            if (appointment == null) throw new RuntimeException("Appointment not found");

            // Ensure doctor updates only their appointments
            if (!appointment.getDoctor().getId().equals(currentDoctor.getId())) {
                redirectAttributes.addFlashAttribute("error", "You can only update your own appointments");
                return "redirect:/doctor/dashboard";
            }

            appointment.setStatus(status);
            appointmentService.saveAppointment(appointment); // Save changes

            redirectAttributes.addFlashAttribute("success", "Appointment status updated successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update appointment status: " + e.getMessage());
        }

        return "redirect:/doctor/dashboard";
    }

    @GetMapping("/settings")
    public String doctorSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName()); // Logged-in user
        return "settings/doctorpreferences";
    }

    @PostMapping("/settings/update")
    public String updateDoctorPreferences(
            @RequestParam("theme") String theme,
            @RequestParam("fontSize") Integer fontSize,
            RedirectAttributes redirectAttributes) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            userService.updatePreferences(username, theme, fontSize); // Save preferences
            redirectAttributes.addFlashAttribute("success", "Preferences updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update preferences: " + e.getMessage());
        }

        return "redirect:/doctor/dashboard";
    }

    private Doctor getCurrentDoctor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Doctor doctor = doctorService.findByEmail(username); // Match doctor by email
            if (doctor != null) {
                ensureDoctorHasUserAccount(doctor, username);
                return doctor;
            }

            List<Doctor> activeDoctors = doctorService.getActiveDoctors(); // Fallback: any active doctor
            if (!activeDoctors.isEmpty()) {
                Doctor fallback = activeDoctors.get(0);
                ensureDoctorHasUserAccount(fallback, username);
                return fallback;
            }

        } catch (Exception e) {
            List<Doctor> allDoctors = doctorService.getAllDoctors(); // Final fallback
            if (!allDoctors.isEmpty()) return allDoctors.get(0);
        }
        return null;
    }

    private void ensureDoctorHasUserAccount(Doctor doctor, String username) {
        try {
            userService.getPreferencesFor(username); // Ensure preferences exist
        } catch (Exception ex) {
            System.out.println("Warning: Doctor user account missing preferences for " + username);
        }
    }
}
