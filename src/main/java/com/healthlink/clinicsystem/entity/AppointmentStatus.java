package com.healthlink.clinicsystem.entity;

public enum AppointmentStatus {

    SCHEDULED("Scheduled"),      // Default status
    COMPLETED("Completed"),      // Appointment done
    CANCELLED("Cancelled"),      // Cancelled by staff/patient
    NO_SHOW("No Show");          // Patient did not attend

    private final String displayName; // User-friendly label

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
