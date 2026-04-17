package com.yair.guzman.employee_service.controller;

import com.yair.guzman.employee_service.dto.EmployeeRequestDTO;
import com.yair.guzman.employee_service.dto.EmployeeResponseDTO;
import com.yair.guzman.employee_service.dto.ErrorResponseDTO;
import com.yair.guzman.employee_service.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Operations for managing employees")
public class EmployeeController {

    private final EmployeeService service;

    @Operation(summary = "Get all employees",
               description = "Returns a list of all registered employees")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get employee by ID",
               description = "Returns a single employee based on their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getById(
            @Parameter(description = "Employee ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Search employees by name",
               description = "Case-insensitive partial search across firstName, middleName, lastName and lastMotherName")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponseDTO>> search(
            @Parameter(description = "Name fragment to search for", example = "john")
            @RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @Operation(summary = "Create one or more employees",
               description = "Accepts a JSON array. Send a single object or multiple in one request")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employees created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<List<EmployeeResponseDTO>> create(
            @RequestBody List<@Valid EmployeeRequestDTO> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveAll(dtos));
    }

    @Operation(summary = "Full update of an employee",
               description = "Replaces ALL fields of the employee. Every field is required.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> fullUpdate(
            @Parameter(description = "Employee ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.ok(service.fullUpdate(id, dto));
    }

    @Operation(summary = "Partial update of an employee",
               description = "Updates only the fields provided in the request body. Omitted fields remain unchanged.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee partially updated"),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> partialUpdate(
            @Parameter(description = "Employee ID", example = "1")
            @PathVariable Long id,
            @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.ok(service.partialUpdate(id, dto));
    }

    @Operation(summary = "Delete an employee",
               description = "Permanently removes an employee by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Employee ID", example = "1")
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}