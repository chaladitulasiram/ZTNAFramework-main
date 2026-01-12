package com.example.ztnaframework.security;

import com.example.ztnaframework.service.DeviceService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("zt") // We name this bean "zt" to use it in annotations like @PreAuthorize("@zt.check(...)")
public class ZeroTrustEvaluator {

    private final DeviceService deviceService;

    public ZeroTrustEvaluator(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * The core ZTNA Decision function.
     * @param auth The user's identity
     * @param targetSegment The network segment trying to be accessed (e.g., 'FINANCE')
     * @param requiredClearance The level required (1-3)
     * @param deviceId The device ID header
     * @return true if access is granted
     */
    public boolean check(Authentication auth, String targetSegment, int requiredClearance, String deviceId) {
        if (!(auth.getPrincipal() instanceof Jwt)) return false;

        Jwt jwt = (Jwt) auth.getPrincipal();

        // 1. EXTRACT ATTRIBUTES (Simulated - in real app, these might come from the JWT claims or a DB lookup)
        // ideally Supabase puts these in the JWT metadata
        String userDept = jwt.getClaimAsString("department"); // Requires custom claims or DB lookup
        Long userClearance = jwt.getClaim("clearance_level") != null ? (Long) jwt.getClaim("clearance_level") : 1L;

        // Note: For this demo, since we didn't customize Supabase JWT generation,
        // we might mock the department check or rely on the caller passing it correctly.
        // Let's assume for this specific logic: if userDept is null, we deny high security segments.

        // 2. CHECK LATERAL MOVEMENT (User Segment vs Target Segment)
        // If target is 'FINANCE', user MUST be 'FINANCE' or 'ADMIN'
        boolean segmentAllowed = targetSegment.equals("PUBLIC") ||
                (userDept != null && userDept.equalsIgnoreCase(targetSegment));

        // 3. CHECK DEVICE HEALTH (Continuous Verification)
        boolean deviceHealthy = deviceService.isDeviceCompliant(deviceId);

        // 4. CHECK CLEARANCE LEVEL
        boolean clearanceSufficient = userClearance >= requiredClearance;

        // THE FINAL DECISION
        return segmentAllowed && deviceHealthy && clearanceSufficient;
    }
}