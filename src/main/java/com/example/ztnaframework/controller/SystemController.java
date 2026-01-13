package com.example.ztnaframework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @GetMapping("/public-status")
    public ResponseEntity<Map<String, Object>> getPublicStatus() {
        Map<String, Object> stats = new HashMap<>();
        // In a real app, inject a Service to get these numbers
        stats.put("activeConnections", 124);
        stats.put("uptime", "12d 4h");
        stats.put("currentRiskScore", 12);
        stats.put("segmentCount", 8);
        return ResponseEntity.ok(stats);
    }
}
