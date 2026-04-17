package com.yair.guzman.employee_service.service;

import com.yair.guzman.employee_service.dto.EmployeeRequestDTO;
import com.yair.guzman.employee_service.dto.EmployeeResponseDTO;
import com.yair.guzman.employee_service.entity.Employee;
import com.yair.guzman.employee_service.exception.EmployeeNotFoundException;
import com.yair.guzman.employee_service.mapper.EmployeeMapper;
import com.yair.guzman.employee_service.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock  EmployeeRepository repository;
    @Mock  EmployeeMapper     mapper;
    @InjectMocks EmployeeServiceImpl service;

    private Employee        employee;
    private EmployeeResponseDTO responseDTO;
    private EmployeeRequestDTO  requestDTO;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L).firstName("John").middleName("A").lastName("Doe")
                .lastMotherName("Smith").age("30").sex("M")
                .birthdate(LocalDate.of(1994, 1, 1)).position("Dev").status(true)
                .build();

        responseDTO = new EmployeeResponseDTO(
                1L, "John", "A", "Doe", "Smith", "30", "M",
                LocalDate.of(1994, 1, 1), "Dev", LocalDateTime.now(), true);

        requestDTO = new EmployeeRequestDTO(
                "John", "A", "Doe", "Smith", "30", "M",
                LocalDate.of(1994, 1, 1), "Dev", true);
    }

    @Test
    @DisplayName("findAll() returns a mapped list of all employees")
    void findAll_returnsMappedList() {
        when(repository.findAll()).thenReturn(List.of(employee));
        when(mapper.toResponseDTO(employee)).thenReturn(responseDTO);

        List<EmployeeResponseDTO> result = service.findAll();

        assertThat(result).hasSize(1).contains(responseDTO);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("findAll() returns empty list when no employees exist")
    void findAll_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.findAll()).isEmpty();
    }

    @Test
    @DisplayName("findById() returns mapped DTO when employee exists")
    void findById_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = service.findById(1L);

        assertThat(result).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("findById() throws EmployeeNotFoundException when employee does not exist")
    void findById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("saveAll() persists all employees and returns mapped DTOs")
    void saveAll_persistsAndReturnsMapped() {
        when(mapper.toEntity(requestDTO)).thenReturn(employee);
        when(repository.saveAll(List.of(employee))).thenReturn(List.of(employee));
        when(mapper.toResponseDTO(employee)).thenReturn(responseDTO);

        List<EmployeeResponseDTO> result = service.saveAll(List.of(requestDTO));

        assertThat(result).hasSize(1).contains(responseDTO);
        verify(repository).saveAll(List.of(employee));
    }

    @Test
    @DisplayName("fullUpdate() updates all fields and returns updated DTO")
    void fullUpdate_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(employee)).thenReturn(employee);
        when(mapper.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = service.fullUpdate(1L, requestDTO);

        assertThat(result).isEqualTo(responseDTO);
        verify(mapper).updateEntity(employee, requestDTO);
        verify(repository).save(employee);
    }

    @Test
    @DisplayName("fullUpdate() throws EmployeeNotFoundException when employee does not exist")
    void fullUpdate_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.fullUpdate(99L, requestDTO))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("partialUpdate() patches only provided fields and returns updated DTO")
    void partialUpdate_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(employee)).thenReturn(employee);
        when(mapper.toResponseDTO(employee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = service.partialUpdate(1L, requestDTO);

        assertThat(result).isEqualTo(responseDTO);
        verify(mapper).partialUpdateEntity(employee, requestDTO);
    }

    @Test
    @DisplayName("partialUpdate() throws EmployeeNotFoundException when employee does not exist")
    void partialUpdate_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.partialUpdate(99L, requestDTO))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("delete() calls deleteById when employee exists")
    void delete_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() throws EmployeeNotFoundException when employee does not exist")
    void delete_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("searchByName() returns employees matching the name fragment")
    void searchByName_returnsMatches() {
        when(repository.searchByName("doe")).thenReturn(List.of(employee));
        when(mapper.toResponseDTO(employee)).thenReturn(responseDTO);

        List<EmployeeResponseDTO> result = service.searchByName("doe");

        assertThat(result).hasSize(1).contains(responseDTO);
    }

    @Test
    @DisplayName("searchByName() returns empty list when no match")
    void searchByName_noMatch() {
        when(repository.searchByName("xyz")).thenReturn(List.of());

        assertThat(service.searchByName("xyz")).isEmpty();
    }
}

