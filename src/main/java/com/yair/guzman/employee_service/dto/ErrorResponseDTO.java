package com.yair.guzman.employee_service.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String message,
        String path,
        Map<String, String> fieldErrors
) {}

