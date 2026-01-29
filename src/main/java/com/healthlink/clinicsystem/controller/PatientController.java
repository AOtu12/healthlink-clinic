package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.Patient;
import com.healthlink.clinicsystem.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // 🔹 LIST
    @GetMapping
    public String listPatients(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("patients", patientService.searchPatients(search));
            model.addAttribute("searchTerm", search);
        } else {
            model.addAttribute("patients", patientService.getAllPatients());
        }
        return "patients/list";
    }

    // 🔹 CREATE FORM
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("patient", patientService.createEmptyPatient());
        return "patients/form";
    }

    // 🔹 EDIT FORM - FIXED
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return patientService.getPatientById(id)
                .map(patient -> {
                    model.addAttribute("patient", patient);
                    return "patients/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Patient not found with ID: " + id);
                    return "redirect:/patients";
                });
    }

    // 🔹 SAVE
    @PostMapping
    public String savePatient(@ModelAttribute("patient") Patient patient,
                              RedirectAttributes ra) {
        boolean isNew = (patient.getId() == null);
        patientService.savePatient(patient);

        if (isNew) {
            ra.addFlashAttribute("success", "Patient added successfully!");
        } else {
            ra.addFlashAttribute("success", "Patient updated successfully!");
        }

        return "redirect:/patients";
    }

    // 🔹 SAFE DELETE
    @PostMapping("/{id}/delete")
    public String deletePatient(@PathVariable Long id, RedirectAttributes ra) {
        try {
            patientService.deletePatient(id);
            ra.addFlashAttribute("success", "Patient deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute(
                    "error",
                    "Cannot delete this patient because there are appointments linked to them. " +
                            "Please delete or update those appointments first."
            );
        }

        return "redirect:/patients";
    }
}