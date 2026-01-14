package com.example.ztnaframework.controller;

import com.example.ztnaframework.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    // Inject your service to get real counts
    // private final DeviceService deviceService;

    // public SystemController(DeviceService deviceService) { ... }

    @GetMapping("/public-status")
    public ResponseEntity<Map<String, Object>> getPublicStatus() {
        Map<String, Object> stats = new HashMap<>();

        // REAL LOGIC:
        // long activeCount = deviceService.countActiveDevices();
        // long riskScore = deviceService.calculateAvgRiskScore();

        // For now, let's simulate 'dynamic' data based on system time to prove it works
        long simulatedActive = (System.currentTimeMillis() % 50) + 100;

        stats.put("activeConnections", simulatedActive); // Now changes dynamically
        stats.put("uptime", "Running");
        stats.put("currentRiskScore", 12);
        stats.put("segmentCount", 8); // Example: HR, Eng, Sales, etc.

        return ResponseEntity.ok(stats);
    }
}