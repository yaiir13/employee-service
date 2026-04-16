package com.yair.guzman.employee_service.service;

import com.yair.guzman.employee_service.dto.EmployeeRequestDTO;
import com.yair.guzman.employee_service.dto.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponseDTO> findAll();
    EmployeeResponseDTO findById(Long id);
    List<EmployeeResponseDTO> saveAll(List<EmployeeRequestDTO> dtos);
    EmployeeResponseDTO fullUpdate(Long id, EmployeeRequestDTO dto);
    EmployeeResponseDTO partialUpdate(Long id, EmployeeRequestDTO dto);
    void delete(Long id);
    List<EmployeeResponseDTO> searchByName(String name);
}

