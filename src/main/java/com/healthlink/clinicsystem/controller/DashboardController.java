package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.Patient;
import com.healthlink.clinicsystem.entity.Appointment;
import com.healthlink.clinicsystem.service.PatientService;
import com.healthlink.clinicsystem.service.DoctorService;
import com.healthlink.clinicsystem.service.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DashboardController(PatientService patientService,
                               DoctorService doctorService,
                               AppointmentService appointmentService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {
        // Get dashboard statistics
        long patientCount = patientService.getPatientCount();
        long activeDoctorCount = doctorService.getActiveDoctorCount();
        long todaysAppointmentCount = appointmentService.getTodaysAppointmentCount();

        // Get recent data for display
        List<Patient> recentPatients = patientService.getRecentPatients();
        List<Appointment> todaysAppointments = appointmentService.getTodaysAppointments();

        // Add data to model
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("doctorCount", activeDoctorCount);
        model.addAttribute("todaysAppointments", todaysAppointmentCount);
        model.addAttribute("recentPatients", recentPatients);
        model.addAttribute("todaysAppointmentList", todaysAppointments);

        return "dashboard/receptionist";
    }

    // ADD THIS METHOD - Login mapping
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}