package com.yair.guzman.employee_service.controller;

import com.yair.guzman.employee_service.dto.LoginRequestDTO;
import com.yair.guzman.employee_service.dto.LoginResponseDTO;
import com.yair.guzman.employee_service.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and obtain JWT token")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Operation(summary = "Login", description = "Authenticate with username and password — returns a Bearer JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful — use the token in Authorization: Bearer <token>"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        String role  = userDetails.getAuthorities().stream()
                .findFirst().map(Object::toString).orElse("ROLE_USER");

        return ResponseEntity.ok(
                new LoginResponseDTO(token, userDetails.getUsername(), role, jwtService.getExpirationMs()));
    }
}

