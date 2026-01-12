package com.example.ztnaframework.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    @GetMapping("/secure-data")
    public Map<String, Object> getSecureResource(@AuthenticationPrincipal Jwt principal) {
        // In a ZTNA model, we log exactly WHO accessed WHAT
        String userId = principal.getSubject();
        String email = principal.getClaimAsString("email");

        return Map.of(
                "status", "Access Granted",
                "message", "This is a segmented secure resource.",
                "user", email,
                "verification_level", "Identity Verified (Phase 1)"
        );
    }
}
