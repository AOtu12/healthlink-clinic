package com.healthlink.clinicsystem.api.dto;

import lombok.Data;

@Data
public class PatientDto {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String gender;
    private Integer age;
}
