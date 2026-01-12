package com.example.ztnaframework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final DevicePostureFilter devicePostureFilter;
    private final MonitoringFilter monitoringFilter;

    // Inject the filters created in Phase 2 and 3
    public SecurityConfig(DevicePostureFilter devicePostureFilter, MonitoringFilter monitoringFilter) {
        this.devicePostureFilter = devicePostureFilter;
        this.monitoringFilter = monitoringFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**", "/api/device/heartbeat").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))

                // ORDER MATTERS:
                // 1. Device Filter runs FIRST to block bad devices early.
                .addFilterAfter(devicePostureFilter, UsernamePasswordAuthenticationFilter.class)

                // 2. Monitoring Filter runs essentially around the request to log the outcome.
                .addFilterAfter(monitoringFilter, DevicePostureFilter.class);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // THIS LINE CONNECTS FRONTEND TO BACKEND
        // It tells Spring Boot: "If a request comes from localhost:5173, allow it."
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

        // THIS ALLOWS THE CUSTOM HEADER WE USE FOR DEVICE CHECKS
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Device-Id"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}