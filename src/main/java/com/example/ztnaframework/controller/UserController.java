package com.example.ztnaframework.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // You should inject a UserRepository here to save to a real DB
    // private final UserRepository userRepository;

    @PostMapping("/sync")
    public ResponseEntity<?> syncUserIdentity(@AuthenticationPrincipal Jwt principal) {
        // 1. Extract details from the Supabase JWT
        String userId = principal.getSubject(); // The UUID from Supabase
        String email = principal.getClaimAsString("email");

        // 2. TODO: Check if user exists in your local DB. If not, save them.
        // if (!userRepository.existsById(userId)) {
        //     User newUser = new User(userId, email, "ACTIVE");
        //     userRepository.save(newUser);
        // }

        System.out.println("Synced User: " + email + " [" + userId + "]");

        return ResponseEntity.ok(Map.of(
                "status", "SYNCED",
                "userId", userId,
                "message", "User identity synchronized with ZTNA Core"
        ));
    }
}
