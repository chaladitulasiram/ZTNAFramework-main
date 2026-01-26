package com.example.ztnaframework.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // --- 👇 PASTE THIS NEW METHOD HERE 👇 ---
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> credentials) {
        // For this mock backend, we treat Register exactly like Login
        // This generates a token immediately so the user enters the dashboard.
        return login(credentials);
    }
    // ----------------------------------------

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");

        // Mock Authentication Logic
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Generate a consistent UUID for this mock user
            String mockUserId = UUID.nameUUIDFromBytes(email.getBytes()).toString();

            // Create HMAC signer
            JWSSigner signer = new MACSigner(jwtSecret.getBytes());

            // Prepare JWT with claims
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(mockUserId)
                    .issuer("http://localhost:8080")
                    .expirationTime(Date.from(Instant.now().plusSeconds(86400))) // 24 hours
                    .claim("roles", "USER")
                    .claim("email", email)
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            Map<String, Object> response = new HashMap<>();
            response.put("token", signedJWT.serialize());
            response.put("user", email);
            response.put("userId", mockUserId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}