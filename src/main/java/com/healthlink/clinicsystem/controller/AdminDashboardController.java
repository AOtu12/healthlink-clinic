package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.Appointment;
import com.healthlink.clinicsystem.entity.AppointmentStatus;
import com.healthlink.clinicsystem.entity.Doctor;
import com.healthlink.clinicsystem.entity.User;
import com.healthlink.clinicsystem.service.AppointmentService;
import com.healthlink.clinicsystem.service.DoctorService;
import com.healthlink.clinicsystem.service.UserService;
import com.healthlink.clinicsystem.service.PatientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    // ------------------------------ DASHBOARD ------------------------------
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalDoctors", doctorService.countDoctors());
        model.addAttribute("todayAppointments", appointmentService.countToday());
        model.addAttribute("cancelledToday", appointmentService.countCancelledToday());
        model.addAttribute("totalPatients", patientService.countPatients());
        return "admin/dashboard";
    }

    // ------------------------------ USER MANAGEMENT ------------------------------
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/create-user";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user, RedirectAttributes ra) {
        userService.createUserWithRole(user);
        ra.addFlashAttribute("success", "User created successfully.");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{username}/edit")
    public String editUserForm(@PathVariable String username, Model model) {
        model.addAttribute("user", userService.findByUsername(username));
        return "admin/edit-user";
    }

    @PostMapping("/users/{username}/edit")
    public String updateUser(@PathVariable String username,
                             @ModelAttribute User updated,
                             RedirectAttributes ra) {
        userService.updateUser(userService.findByUsername(username).getId(), updated);
        ra.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{username}/toggle-active")
    public String toggleUserActive(@PathVariable String username, RedirectAttributes ra) {
        userService.toggleActive(userService.findByUsername(username).getId());
        ra.addFlashAttribute("success", "Status updated.");
        return "redirect:/admin/users";
    }

    // ------------------------------ DOCTOR MANAGEMENT ------------------------------
    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "admin/doctors";
    }

    @GetMapping("/doctors/create")
    public String createDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "admin/create-doctor";
    }

    @PostMapping("/doctors/create")
    public String createDoctor(@ModelAttribute Doctor doctor, RedirectAttributes ra) {
        doctorService.saveDoctor(doctor);
        ra.addFlashAttribute("success", "Doctor created.");
        return "redirect:/admin/doctors";
    }

    // ===========================================================================
    // ⭐ FIXED: All doctor operations now use ID instead of email
    // ===========================================================================

    // 🔹 EDIT DOCTOR FORM (GET) - Using ID
    @GetMapping("/doctors/{id}/edit")
    public String editDoctorForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return doctorService.getDoctorById(id)
                .map(doctor -> {
                    model.addAttribute("doctor", doctor);
                    return "admin/edit-doctor";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Doctor not found with ID: " + id);
                    return "redirect:/admin/doctors";
                });
    }

    // 🔹 UPDATE DOCTOR (POST) - Using ID
    @PostMapping("/doctors/{id}/edit")
    public String updateDoctor(@PathVariable Long id,
                               @ModelAttribute Doctor updated,
                               RedirectAttributes ra) {
        try {
            doctorService.updateDoctor(id, updated);
            ra.addFlashAttribute("success", "Doctor updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to update doctor: " + e.getMessage());
        }
        return "redirect:/admin/doctors";
    }

    // 🔹 TOGGLE ACTIVE STATUS - Using ID (FIXED: No more email lookup)
    @PostMapping("/doctors/{id}/toggle-active")
    public String toggleDoctor(@PathVariable Long id, RedirectAttributes ra) {
        try {
            doctorService.toggleActive(id);
            ra.addFlashAttribute("success", "Status updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/doctors";
    }

    // ------------------------------ APPOINTMENTS ------------------------------
    @GetMapping("/appointments")
    public String appointments(@RequestParam(required = false) String doctorEmail,
                               @RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                               LocalDate date,
                               @RequestParam(required = false) AppointmentStatus status,
                               Model model) {

        List<Appointment> appointments =
                appointmentService.searchAdminByEmail(doctorEmail, date, status);

        model.addAttribute("appointments", appointments);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("selectedDoctorEmail", doctorEmail);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("allStatuses", AppointmentStatus.values());

        return "admin/appointments";
    }

    // ------------------------------ REPORTS ------------------------------
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("totalAppointments", appointmentService.countAll());
        model.addAttribute("totalPatients", patientService.countPatients());
        model.addAttribute("totalCancellations", appointmentService.countCancelledAll());
        return "admin/reports";
    }

    @GetMapping("/reports/appointments/csv")
    public ResponseEntity<Resource> exportCsv(@RequestParam LocalDate startDate,
                                              @RequestParam LocalDate endDate) {

        List<Appointment> appointments = appointmentService.findBetweenDates(startDate, endDate);

        String header = "Date,Time,Doctor,Patient,Status\n";

        String body = appointments.stream()
                .map(a -> String.format("%s,%s,%s,%s,%s",
                        a.getAppointmentDate(),
                        a.getAppointmentTime(),
                        a.getDoctor().getFullName(),
                        a.getPatient().getFullName(),
                        a.getStatus()))
                .collect(Collectors.joining("\n"));

        byte[] bytes = (header + body).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=appointments.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(new ByteArrayResource(bytes));
    }
}