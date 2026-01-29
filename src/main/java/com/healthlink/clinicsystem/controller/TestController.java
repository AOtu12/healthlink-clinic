package com.healthlink.clinicsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/simple-test")
    public String simpleTest(Model model) {
        model.addAttribute("message", "Simple test works!");
        return "simple-test";
    }
}