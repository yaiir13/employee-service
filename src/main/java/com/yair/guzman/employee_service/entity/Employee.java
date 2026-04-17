package com.yair.guzman.employee_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    @Column(name = "middle_name", length = 50)
    private String middleName;

    @NotBlank(message = "Last name (paternal) is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Last mother name (maternal) is required")
    @Size(min = 2, max = 50, message = "Last mother name must be between 2 and 50 characters")
    @Column(name = "last_mother_name", nullable = false, length = 50)
    private String lastMotherName;

    @NotBlank(message = "Age is required")
    @Pattern(regexp = "^(1[0-9]|[2-9][0-9]|1[0-4][0-9]|150)$",
             message = "Age must be a valid number between 18 and 150")
    @Column(name = "age", nullable = false, length = 3)
    private String age;

    @NotNull(message = "Sex is required")
    @Pattern(regexp = "^[MFO]$", message = "Sex must be M (Male), F (Female) or O (Other)")
    @Column(name = "sex", nullable = false, length = 1)
    private String sex;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be a past date")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthdate;

    @NotBlank(message = "Position is required")
    @Size(min = 2, max = 100, message = "Position must be between 2 and 100 characters")
    @Column(name = "position", nullable = false, length = 100)
    private String position;

    @CreationTimestamp
    @Column(name = "system_registration_date", nullable = false, updatable = false)
    private LocalDateTime systemRegistrationDate;

    @Builder.Default
    @Column(name = "status", nullable = false)
    private boolean status = true;
}