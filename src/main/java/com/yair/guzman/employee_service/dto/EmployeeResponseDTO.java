package com.yair.guzman.employee_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeResponseDTO(
        Long id,
        String firstName,
        String middleName,
        String lastName,
        String lastMotherName,
        String age,
        String sex,
        LocalDate birthdate,
        String position,
        LocalDateTime systemRegistrationDate,
        boolean status
) {}