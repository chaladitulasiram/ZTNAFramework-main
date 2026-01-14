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
        List<UserDevice> devices = deviceRepository.findAll();
        return ResponseEntity.ok(devices);
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<?> receiveHeartbeat(
            @AuthenticationPrincipal Jwt principal,
            @RequestBody DevicePostureDTO posture) {

        // FIX: Check if principal is null (Anonymous user)
        if (principal == null) {
            // If user is not logged in, we cannot link the device to a user.
            // We return OK to stop frontend errors, but we don't save anything.
            return ResponseEntity.ok(Map.of(
                    "status", "IGNORED",
                    "message", "Heartbeat ignored: User not logged in."
            ));
        }

        // If logged in, proceed as normal
        posture.setUserId(principal.getSubject());
        boolean isHealthy = deviceService.evaluatePosture(posture);

        if (isHealthy) {
            return ResponseEntity.ok(Map.of("status", "COMPLIANT", "message", "Access granted."));
        } else {
            return ResponseEntity.status(403).body(Map.of("status", "NON_COMPLIANT", "message", "Device unsafe."));
        }
    }
}