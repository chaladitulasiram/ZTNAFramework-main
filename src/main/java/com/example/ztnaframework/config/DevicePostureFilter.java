package com.example.ztnaframework.config;

import com.example.ztnaframework.service.DeviceService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class DevicePostureFilter extends OncePerRequestFilter {

    private final DeviceService deviceService;

    public DevicePostureFilter(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // FIX: Added "/api/system/public-status" to the exclusion list
        if (path.startsWith("/api/public") ||
                path.startsWith("/api/device/heartbeat") ||
                path.startsWith("/api/system/public-status")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Check if Device ID header exists
        String deviceId = request.getHeader("X-Device-Id");

        if (deviceId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "ZTNA Policy Violation: No Device ID Header found.");
            return;
        }

        // 2. Check if that Device is Compliant (Verified by DeviceService)
        if (!deviceService.isDeviceCompliant(deviceId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "ZTNA Policy Violation: Device is Non-Compliant or Unregistered.");
            return;
        }

        // 3. If valid, proceed to the next security chain
        filterChain.doFilter(request, response);
    }
}