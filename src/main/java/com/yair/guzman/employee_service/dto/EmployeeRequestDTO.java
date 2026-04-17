package com.yair.guzman.employee_service.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EmployeeRequestDTO(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Size(max = 50, message = "Middle name must not exceed 50 characters")
        String middleName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotBlank(message = "Last mother name is required")
        @Size(min = 2, max = 50, message = "Last mother name must be between 2 and 50 characters")
        String lastMotherName,

        @NotBlank(message = "Age is required")
        @Pattern(regexp = "^(1[0-9]|[2-9][0-9]|1[0-4][0-9]|150)$",
                 message = "Age must be a valid number between 18 and 150")
        String age,

        @NotNull(message = "Sex is required")
        @Pattern(regexp = "^[MFO]$", message = "Sex must be M (Male), F (Female) or O (Other)")
        String sex,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be a past date")
        LocalDate birthdate,

        @NotBlank(message = "Position is required")
        @Size(min = 2, max = 100, message = "Position must be between 2 and 100 characters")
        String position,

        Boolean status
) {}