package com.example.ztnaframework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @GetMapping("/public-status")
    public ResponseEntity<?> getPublicStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "OPERATIONAL",
                "version", "1.0.0",
                "message", "ZTNA Gateway is running"
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<?> getHealth() {
        return ResponseEntity.ok(Map.of("db", "connected", "latency", "12ms"));
    }
}