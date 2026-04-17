package com.yair.guzman.employee_service.dto;

public record LoginResponseDTO(
        String token,
        String type,
        String username,
        String role,
        long expiresIn
) {
    public LoginResponseDTO(String token, String username, String role, long expiresIn) {
        this(token, "Bearer", username, role, expiresIn);
    }
}

