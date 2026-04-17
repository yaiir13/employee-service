package com.yair.guzman.employee_service.mapper;

import com.yair.guzman.employee_service.dto.EmployeeRequestDTO;
import com.yair.guzman.employee_service.dto.EmployeeResponseDTO;
import com.yair.guzman.employee_service.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class  EmployeeMapper {

    public Employee toEntity(EmployeeRequestDTO dto) {
        return Employee.builder()
                .firstName(dto.firstName())
                .middleName(dto.middleName())
                .lastName(dto.lastName())
                .lastMotherName(dto.lastMotherName())
                .age(dto.age())
                .sex(dto.sex())
                .birthdate(dto.birthdate())
                .position(dto.position())
                .status(dto.status() != null ? dto.status() : true)
                .build();
    }

    public EmployeeResponseDTO toResponseDTO(Employee e) {
        return new EmployeeResponseDTO(
                e.getId(),
                e.getFirstName(),
                e.getMiddleName(),
                e.getLastName(),
                e.getLastMotherName(),
                e.getAge(),
                e.getSex(),
                e.getBirthdate(),
                e.getPosition(),
                e.getSystemRegistrationDate(),
                e.isStatus()
        );
    }

    /** Full replacement — used by PUT */
    public void updateEntity(Employee employee, EmployeeRequestDTO dto) {
        employee.setFirstName(dto.firstName());
        employee.setMiddleName(dto.middleName());
        employee.setLastName(dto.lastName());
        employee.setLastMotherName(dto.lastMotherName());
        employee.setAge(dto.age());
        employee.setSex(dto.sex());
        employee.setBirthdate(dto.birthdate());
        employee.setPosition(dto.position());
        if (dto.status() != null) employee.setStatus(dto.status());
    }

    /** Partial update — used by PATCH */
    public void partialUpdateEntity(Employee employee, EmployeeRequestDTO dto) {
        if (dto.firstName()      != null) employee.setFirstName(dto.firstName());
        if (dto.middleName()     != null) employee.setMiddleName(dto.middleName());
        if (dto.lastName()       != null) employee.setLastName(dto.lastName());
        if (dto.lastMotherName() != null) employee.setLastMotherName(dto.lastMotherName());
        if (dto.age()            != null) employee.setAge(dto.age());
        if (dto.sex()            != null) employee.setSex(dto.sex());
        if (dto.birthdate()      != null) employee.setBirthdate(dto.birthdate());
        if (dto.position()       != null) employee.setPosition(dto.position());
        if (dto.status()         != null) employee.setStatus(dto.status());
    }
}