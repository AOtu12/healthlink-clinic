package com.healthlink.clinicsystem.controller;

import com.healthlink.clinicsystem.entity.Doctor;
import com.healthlink.clinicsystem.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    // 🔹 LIST PAGE
    @GetMapping
    public String listDoctors(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("doctors", doctorService.searchDoctors(search));
            model.addAttribute("searchTerm", search);
        } else {
            model.addAttribute("doctors", doctorService.getAllDoctors());
        }
        return "doctors/list";
    }

    // 🔹 CREATE FORM
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("doctor", doctorService.createEmptyDoctor());
        return "doctors/form";
    }

    // 🔹 EDIT FORM - FIXED
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return doctorService.getDoctorById(id)
                .map(doctor -> {
                    model.addAttribute("doctor", doctor);
                    return "doctors/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Doctor not found with ID: " + id);
                    return "redirect:/doctors";
                });
    }

    // 🔹 SAVE (CREATE / UPDATE)
    @PostMapping
    public String saveDoctor(@ModelAttribute("doctor") Doctor doctor,
                             RedirectAttributes ra) {
        boolean isNew = (doctor.getId() == null);
        doctorService.saveDoctor(doctor);

        if (isNew) {
            ra.addFlashAttribute("success", "Doctor added successfully!");
        } else {
            ra.addFlashAttribute("success", "Doctor updated successfully!");
        }

        return "redirect:/doctors";
    }

    // 🔹 SAFE DELETE
    @PostMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes ra) {
        try {
            doctorService.deleteDoctor(id);
            ra.addFlashAttribute("success", "Doctor deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute(
                    "error",
                    "Cannot delete this doctor because there are appointments linked to them. " +
                            "Please delete or reassign those appointments first."
            );
        }

        return "redirect:/doctors";
    }
}