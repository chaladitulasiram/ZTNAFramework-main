package com.example.ztnaframework.controller;

import com.example.ztnaframework.model.AccessLog;
import com.example.ztnaframework.repository.AccessLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    private final AccessLogRepository accessLogRepository;

    public ResourceController(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    // --- NEW: Real Audit Logs ---
    @GetMapping("/logs")
    public ResponseEntity<List<AccessLog>> getRealLogs() {
        // Returns all logs (In production, use pagination!)
        return ResponseEntity.ok(accessLogRepository.findAll());
    }

    // Mock Segments (Since we don't have a Segment Table yet, we define real protected zones)
    @GetMapping("/segments")
    public ResponseEntity<List<String>> getSegments() {
        return ResponseEntity.ok(List.of(
                "HR Database (Secure)",
                "Engineering-Prod (Encrypted)",
                "Salesforce-Gateway",
                "Internal-Wiki"
        ));
    }
}