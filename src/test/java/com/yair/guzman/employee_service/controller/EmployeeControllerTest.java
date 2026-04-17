package com.yair.guzman.employee_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yair.guzman.employee_service.config.SecurityConfig;
import com.yair.guzman.employee_service.dto.EmployeeRequestDTO;
import com.yair.guzman.employee_service.dto.EmployeeResponseDTO;
import com.yair.guzman.employee_service.exception.EmployeeNotFoundException;
import com.yair.guzman.employee_service.repository.AuthUserRepository;
import com.yair.guzman.employee_service.security.JwtService;
import com.yair.guzman.employee_service.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(SecurityConfig.class)
class EmployeeControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean EmployeeService    employeeService;
    @MockitoBean JwtService         jwtService;
    @MockitoBean AuthUserRepository authUserRepository;

    private ObjectMapper objectMapper;
    private EmployeeResponseDTO responseDTO;
    private EmployeeRequestDTO  requestDTO;
    private static final String BASE_URL = "/api/v1/employees";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        responseDTO = new EmployeeResponseDTO(
                1L, "John", "A", "Doe", "Smith", "30", "M",
                LocalDate.of(1994, 1, 1), "Developer", LocalDateTime.now(), true);

        requestDTO = new EmployeeRequestDTO(
                "John", "A", "Doe", "Smith", "30", "M",
                LocalDate.of(1994, 1, 1), "Developer", true);
    }

    @Test
    @DisplayName("GET /employees returns 200 with list of employees")
    void getAll_returns200() throws Exception {
        when(employeeService.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get(BASE_URL).with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @DisplayName("GET /employees returns 200 with empty list when no employees")
    void getAll_returnsEmptyList() throws Exception {
        when(employeeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL).with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /employees returns 401/403 when unauthenticated")
    void getAll_returns4xx_whenUnauthenticated() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("GET /employees/{id} returns 200 when employee found")
    void getById_returns200() throws Exception {
        when(employeeService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get(BASE_URL + "/1").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @DisplayName("GET /employees/{id} returns 404 when employee not found")
    void getById_returns404() throws Exception {
        when(employeeService.findById(99L)).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get(BASE_URL + "/99").with(user("user").roles("USER")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /employees/search returns 200 with matching employees")
    void search_returns200() throws Exception {
        when(employeeService.searchByName("doe")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get(BASE_URL + "/search").param("name", "doe")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    @DisplayName("GET /employees/search returns 200 with empty list when no match")
    void search_returnsEmptyList() throws Exception {
        when(employeeService.searchByName("xyz")).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/search").param("name", "xyz")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("POST /employees returns 201 when payload is valid")
    void create_returns201() throws Exception {
        when(employeeService.saveAll(anyList())).thenReturn(List.of(responseDTO));

        mockMvc.perform(post(BASE_URL).with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(requestDTO))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("POST /employees returns 400 when required fields are missing")
    void create_returns400_onValidationFailure() throws Exception {
        EmployeeRequestDTO invalid = new EmployeeRequestDTO(
                "", null, "Doe", "Smith", "30", "M",
                LocalDate.of(1994, 1, 1), "Developer", null);

        mockMvc.perform(post(BASE_URL).with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(invalid))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PUT /employees/{id} returns 200 when employee updated")
    void fullUpdate_returns200() throws Exception {
        when(employeeService.fullUpdate(eq(1L), any())).thenReturn(responseDTO);

        mockMvc.perform(put(BASE_URL + "/1").with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /employees/{id} returns 404 when employee does not exist")
    void fullUpdate_returns404() throws Exception {
        when(employeeService.fullUpdate(eq(99L), any())).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(put(BASE_URL + "/99").with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /employees/{id} returns 400 on validation failure")
    void fullUpdate_returns400_onValidationFailure() throws Exception {
        EmployeeRequestDTO invalid = new EmployeeRequestDTO(
                "", null, "Doe", "Smith", "30", "M",
                LocalDate.of(1994, 1, 1), "Developer", null);

        mockMvc.perform(put(BASE_URL + "/1").with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /employees/{id} returns 200 when partial update succeeds")
    void partialUpdate_returns200() throws Exception {
        when(employeeService.partialUpdate(eq(1L), any())).thenReturn(responseDTO);

        EmployeeRequestDTO patch = new EmployeeRequestDTO(
                "Jane", null, null, null, null, null, null, null, null);

        mockMvc.perform(patch(BASE_URL + "/1").with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PATCH /employees/{id} returns 404 when employee does not exist")
    void partialUpdate_returns404() throws Exception {
        when(employeeService.partialUpdate(eq(99L), any())).thenThrow(new EmployeeNotFoundException(99L));

        EmployeeRequestDTO patch = new EmployeeRequestDTO(
                "Jane", null, null, null, null, null, null, null, null);

        mockMvc.perform(patch(BASE_URL + "/99").with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /employees/{id} returns 204 when employee deleted")
    void delete_returns204() throws Exception {
        doNothing().when(employeeService).delete(1L);

        mockMvc.perform(delete(BASE_URL + "/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(employeeService).delete(1L);
    }

    @Test
    @DisplayName("DELETE /employees/{id} returns 404 when employee does not exist")
    void delete_returns404() throws Exception {
        doThrow(new EmployeeNotFoundException(99L)).when(employeeService).delete(99L);

        mockMvc.perform(delete(BASE_URL + "/99").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }
}