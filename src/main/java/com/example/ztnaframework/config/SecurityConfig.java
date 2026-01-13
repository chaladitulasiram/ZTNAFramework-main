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
                        // UPDATE: Added "/api/system/public-status" to the whitelist
                        .requestMatchers("/api/public/**", "/api/device/heartbeat", "/api/system/public-status").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
                // 1. Block bad devices immediately
                .addFilterAfter(devicePostureFilter, UsernamePasswordAuthenticationFilter.class)
                // 2. Log outcome of the authorized/device-checked request
                .addFilterAfter(monitoringFilter, DevicePostureFilter.class);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        // Ensure "X-Device-Id" is allowed, as the frontend sends it
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Device-Id"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}