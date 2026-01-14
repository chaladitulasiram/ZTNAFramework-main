package com.example.ztnaframework.controller;

import com.example.ztnaframework.repository.AccessLogRepository;
import com.example.ztnaframework.repository.DeviceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final DeviceRepository deviceRepository;
    private final AccessLogRepository accessLogRepository;

    public SystemController(DeviceRepository deviceRepository, AccessLogRepository accessLogRepository) {
        this.deviceRepository = deviceRepository;
        this.accessLogRepository = accessLogRepository;
    }

    // Public endpoint for Landing Page
    @GetMapping("/public-status")
    public ResponseEntity<Map<String, Object>> getPublicStatus() {
        return ResponseEntity.ok(generateRealStats());
    }

    // Authenticated endpoint for Dashboard (can add more sensitive details here if needed)
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        return ResponseEntity.ok(generateRealStats());
    }

    private Map<String, Object> generateRealStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Real Active Connections (Count of devices in DB)
        long activeDevices = deviceRepository.count();
        stats.put("activeConnections", activeDevices);

        // 2. Real Uptime (JVM Uptime)
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        Duration duration = Duration.ofMillis(uptimeMillis);
        String uptime = String.format("%dd %dh %dm",
                duration.toDays(),
                duration.toHoursPart(),
                duration.toMinutesPart());
        stats.put("uptime", uptime);

        // 3. Real Risk Score (Example: Calculate based on recent non-200 logs)
        // For simplicity, we default to a low score unless we find 'BLOCK' logs
        long blockedCount = accessLogRepository.count(); // In real logic, filter by outcome="BLOCK"
        stats.put("currentRiskScore", Math.min(100, blockedCount * 5));

        // 4. Segments (Hardcoded or derived from distinct API endpoints accessed)
        stats.put("segmentCount", 4); // Keeping simple, or query distinct endpoints

        return stats;
    }
}