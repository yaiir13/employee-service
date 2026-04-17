package com.yair.guzman.employee_service.config;

import com.yair.guzman.employee_service.entity.AuthUser;
import com.yair.guzman.employee_service.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        if (authUserRepository.findByUsername("admin").isEmpty()) {
            authUserRepository.save(AuthUser.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ROLE_ADMIN")
                    .build());
            log.info("Seeded user: admin / admin123 (ROLE_ADMIN)");
        }

        if (authUserRepository.findByUsername("user").isEmpty()) {
            authUserRepository.save(AuthUser.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .role("ROLE_USER")
                    .build());
            log.info("Seeded user: user / user123 (ROLE_USER)");
        }
    }
}