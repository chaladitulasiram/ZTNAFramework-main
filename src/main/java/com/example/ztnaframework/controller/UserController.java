package com.example.ztnaframework.controller;

import com.example.ztnaframework.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final DeviceService deviceService;

    // Inject the service
    public UserController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncUser(
            @AuthenticationPrincipal Jwt principal,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId) {

        String email = principal.getClaimAsString("email");
        String userId = principal.getSubject();

        System.out.println("🔄 Received Sync Request: " + email);

        if (deviceId != null && !deviceId.isEmpty()) {
            // Call the service to save to DB
            deviceService.registerDevice(deviceId, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "SYNCED",
                    "userId", userId,
                    "deviceId", deviceId,
                    "message", "User and Device successfully registered in database"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "X-Device-Id header missing"));
        }
    }
}