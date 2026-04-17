package com.yair.guzman.employee_service.service;

import com.yair.guzman.employee_service.dto.EmployeeRequestDTO;
import com.yair.guzman.employee_service.dto.EmployeeResponseDTO;
import com.yair.guzman.employee_service.entity.Employee;
import com.yair.guzman.employee_service.exception.EmployeeNotFoundException;
import com.yair.guzman.employee_service.mapper.EmployeeMapper;
import com.yair.guzman.employee_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> findAll() {
        log.debug("Fetching all employees");
        return repository.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO findById(Long id) {
        log.debug("Fetching employee with id={}", id);
        return mapper.toResponseDTO(findOrThrow(id));
    }

    @Override
    public List<EmployeeResponseDTO> saveAll(List<EmployeeRequestDTO> dtos) {
        log.debug("Saving {} employee(s)", dtos.size());
        List<Employee> employees = dtos.stream()
                .map(mapper::toEntity)
                .toList();
        List<EmployeeResponseDTO> saved = repository.saveAll(employees).stream()
                .map(mapper::toResponseDTO)
                .toList();
        log.info("Saved {} employee(s) successfully", saved.size());
        return saved;
    }

    @Override
    public EmployeeResponseDTO fullUpdate(Long id, EmployeeRequestDTO dto) {
        log.debug("Full update for employee id={}", id);
        Employee employee = findOrThrow(id);
        mapper.updateEntity(employee, dto);
        EmployeeResponseDTO result = mapper.toResponseDTO(repository.save(employee));
        log.info("Full update completed for employee id={}", id);
        return result;
    }

    @Override
    public EmployeeResponseDTO partialUpdate(Long id, EmployeeRequestDTO dto) {
        log.debug("Partial update for employee id={}", id);
        Employee employee = findOrThrow(id);
        mapper.partialUpdateEntity(employee, dto);
        EmployeeResponseDTO result = mapper.toResponseDTO(repository.save(employee));
        log.info("Partial update completed for employee id={}", id);
        return result;
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting employee id={}", id);
        findOrThrow(id);
        repository.deleteById(id);
        log.info("Deleted employee id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> searchByName(String name) {
        log.debug("Searching employees by name='{}'", name);
        return repository.searchByName(name).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    private Employee findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }
}