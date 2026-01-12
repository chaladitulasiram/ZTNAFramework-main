package com.example.ztnaframework.controller;


import com.example.ztnaframework.model.DevicePostureDTO;
import com.example.ztnaframework.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<?> receiveHeartbeat(
            @AuthenticationPrincipal Jwt principal,
            @RequestBody DevicePostureDTO posture) {

        // Link the device to the authenticated user
        posture.setUserId(principal.getSubject());

        boolean isHealthy = deviceService.evaluatePosture(posture);

        if (isHealthy) {
            return ResponseEntity.ok(Map.of(
                    "status", "COMPLIANT",
                    "message", "Device posture verified. Access granted."
            ));
        } else {
            return ResponseEntity.status(403).body(Map.of(
                    "status", "NON_COMPLIANT",
                    "message", "Device unsafe. Turn on Firewall/Encryption."
            ));
        }
    }
}
