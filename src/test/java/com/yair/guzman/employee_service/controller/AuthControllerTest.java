package com.yair.guzman.employee_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yair.guzman.employee_service.config.SecurityConfig;
import com.yair.guzman.employee_service.dto.LoginRequestDTO;
import com.yair.guzman.employee_service.repository.AuthUserRepository;
import com.yair.guzman.employee_service.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean AuthenticationManager authenticationManager;
    @MockitoBean JwtService            jwtService;
    @MockitoBean AuthUserRepository    authUserRepository;

    private ObjectMapper objectMapper;
    private static final String LOGIN_URL = "/api/v1/auth/login";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /auth/login returns 200 with token on valid credentials")
    void login_returns200_withToken() throws Exception {
        UserDetails userDetails = new User(
                "admin", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(userDetails)).thenReturn("mocked.jwt.token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        LoginRequestDTO request = new LoginRequestDTO("admin", "admin123");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    @DisplayName("POST /auth/login returns 401 on invalid credentials")
    void login_returns401_onBadCredentials() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequestDTO request = new LoginRequestDTO("admin", "wrong");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /auth/login returns 400 when username is blank")
    void login_returns400_whenUsernameBlank() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("", "admin123");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /auth/login returns 400 when password is blank")
    void login_returns400_whenPasswordBlank() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("admin", "");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /auth/login returns 400 when body is empty")
    void login_returns400_whenBodyEmpty() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}