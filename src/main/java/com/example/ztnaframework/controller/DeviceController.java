package com.example.ztnaframework.controller;

import com.example.ztnaframework.model.DevicePostureDTO;
import com.example.ztnaframework.model.UserDevice;
import com.example.ztnaframework.repository.DeviceRepository;
import com.example.ztnaframework.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceRepository deviceRepository;

    public DeviceController(DeviceService deviceService, DeviceRepository deviceRepository) {
        this.deviceService = deviceService;
        this.deviceRepository = deviceRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDevice>> getAllDevices() {
        return ResponseEntity.ok(deviceRepository.findAll());
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<?> receiveHeartbeat(
            @AuthenticationPrincipal Jwt principal,
            @RequestBody DevicePostureDTO posture) {

        if (principal != null) {
            // Set the User ID from the authenticated token
            posture.setUserId(principal.getSubject());
        }

        // evaluatePosture will now SAVE the device to the DB
        boolean isHealthy = deviceService.evaluatePosture(posture);

        if (isHealthy) {
            return ResponseEntity.ok(Map.of("status", "COMPLIANT", "message", "Device verified."));
        } else {
            return ResponseEntity.status(403).body(Map.of("status", "NON_COMPLIANT", "message", "Security checks failed."));
        }
    }
}