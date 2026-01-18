package com.example.ztnaframework.service;

import com.example.ztnaframework.model.DevicePostureDTO;
import com.example.ztnaframework.model.UserDevice;
import com.example.ztnaframework.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    // --- 1. Evaluate Posture (Heartbeat) ---
    @Transactional
    public boolean evaluatePosture(DevicePostureDTO dto) {
        int score = 0;
        if (dto.getOsType() != null) score += 20;
        if (dto.isFirewallOn()) score += 30;
        if (dto.isDiskEncrypted()) score += 30;
        if (dto.isAntivirusEnabled()) score += 20;

        boolean isCompliant = score >= 80;
        String status = isCompliant ? "COMPLIANT" : "NON_COMPLIANT";

        // Fetch existing or create new
        UserDevice device = deviceRepository.findById(dto.getDeviceId())
                .orElse(new UserDevice());

        device.setDeviceId(dto.getDeviceId());

        // Fix: Handle UUID Conversion
        if (dto.getUserId() != null && !dto.getUserId().isEmpty()) {
            try {
                device.setUserId(UUID.fromString(dto.getUserId()));
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Invalid UUID format in Heartbeat: " + dto.getUserId());
            }
        }

        device.setOsType(dto.getOsType());
        device.setOsVersion(dto.getOsVersion());
        device.setFirewallOn(dto.isFirewallOn());
        device.setDiskEncrypted(dto.isDiskEncrypted());
        device.setStatus(status);
        device.setLastSeen(LocalDateTime.now());

        deviceRepository.save(device); // This saves the update

        return isCompliant;
    }

    // --- 2. Register Device (New User Sync) ---
    @Transactional
    public void registerDevice(String deviceId, String userIdString) {
        // FIX: Don't just check 'exists'. We must fetch and UPDATE the user ID.
        UserDevice device = deviceRepository.findById(deviceId)
                .orElse(new UserDevice());

        device.setDeviceId(deviceId);

        // Fix: Handle UUID Conversion safely
        if (userIdString != null) {
            try {
                device.setUserId(UUID.fromString(userIdString));
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Error: User ID is not a valid UUID: " + userIdString);
                return; // Stop if ID is invalid
            }
        }

        // Only set defaults if they are missing (preserve heartbeat data if it exists)
        if (device.getStatus() == null) device.setStatus("COMPLIANT");
        if (device.getOsType() == null) device.setOsType("Windows"); // Default
        if (device.getOsVersion() == null) device.setOsVersion("11");

        // Ensure timestamp is current
        device.setLastSeen(LocalDateTime.now());

        // Always save (Upsert)
        deviceRepository.save(device);
        System.out.println("✅ Database: Device synced with User " + userIdString);
    }

    // --- 3. Check Compliance (Filter) ---
    public boolean isDeviceCompliant(String deviceId) {
        if (deviceId == null) return false;
        return deviceRepository.findById(deviceId)
                .map(device -> "COMPLIANT".equals(device.getStatus()))
                .orElse(false);
    }
}