package com.example.ztnaframework.config;

import com.example.ztnaframework.model.AccessLog;
import com.example.ztnaframework.service.MonitoringService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class MonitoringFilter extends OncePerRequestFilter {

    private final MonitoringService monitoringService;

    public MonitoringFilter(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            // Proceed with the request chain (Authentication -> Device Check -> Controller)
            filterChain.doFilter(request, response);
        } finally {
            // This block runs AFTER the response is committed
            recordActivity(request, response);
        }
    }

    private void recordActivity(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = null;
        String email = "anonymous";
        String deviceId = request.getHeader("X-Device-Id");

        if (auth != null && auth.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) auth.getPrincipal();
            userId = jwt.getSubject();
            email = jwt.getClaimAsString("email");
        }

        AccessLog log = AccessLog.builder()
                .timestamp(LocalDateTime.now())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .ipAddress(request.getRemoteAddr())
                .deviceId(deviceId != null ? deviceId : "unknown")
                .userId(userId)
                .userEmail(email)
                .statusCode(response.getStatus())
                .outcome(response.getStatus() >= 200 && response.getStatus() < 300 ? "ALLOWED" : "DENIED")
                .build();

        // Send to async service to avoid slowing down user
        monitoringService.logEvent(log);
    }
}