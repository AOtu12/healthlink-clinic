package com.healthlink.clinicsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(nullable = false)
    private String theme = "light"; // UI theme

    @Column(name = "font_size", nullable = false)
    private Integer fontSize = 14; // UI font size

    @OneToOne(mappedBy = "preferences")
    private User user; // Linked user

    public UserPreferences() {}

    public UserPreferences(String theme, Integer fontSize) {
        this.theme = theme;
        this.fontSize = fontSize;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public Integer getFontSize() { return fontSize; }
    public void setFontSize(Integer fontSize) {
        // Validate range
        if (fontSize >= 12 && fontSize <= 24) {
            this.fontSize = fontSize;
        } else {
            this.fontSize = 14; // Default
        }
    }

    // CSS helper for theme
    public String getThemeClass() {
        switch (theme) {
            case "dark": return "theme-dark";
            case "auto": return "theme-auto";
            default: return "theme-light";
        }
    }

    // CSS helper for font size
    public String getFontSizeClass() {
        if (fontSize <= 12) return "text-xs";
        if (fontSize <= 14) return "text-sm";
        if (fontSize <= 16) return "text-base";
        if (fontSize <= 18) return "text-lg";
        return "text-xl";
    }
}
