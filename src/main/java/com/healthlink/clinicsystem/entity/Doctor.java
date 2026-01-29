package com.healthlink.clinicsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(name = "first_name", nullable = false)
    private String firstName; // First name

    @Column(name = "last_name", nullable = false)
    private String lastName; // Last name

    private String specialization; // Doctor specialty
    private String email;          // Login/Contact email
    private String phone;          // Contact number

    private Boolean active = true; // Status flag

    public Doctor() {}

    public Doctor(String firstName, String lastName, String specialization, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.email = email;
        this.phone = phone;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean isActive() { return active; } // Active status
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getFullName() { return this.firstName + " " + this.lastName; } // Helper
}
