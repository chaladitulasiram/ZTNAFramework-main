package com.example.ztnaframework.service;

import com.example.ztnaframework.model.DevicePostureDTO;
import com.example.ztnaframework.model.UserDevice;
import com.example.ztnaframework.repository.DeviceRepository;
import jakarta.transaction.Transactional; // Ensure this import is present
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

        // Corrected Getter Calls (Lombok 'is' prefix for booleans)
        if (dto.getOsType() != null) score += 20;
        if (dto.isFirewallOn()) score += 30;     // Fixed Name
        if (dto.isDiskEncrypted()) score += 30;  // Fixed Name
        if (dto.isAntivirusEnabled()) score += 20;

        boolean isCompliant = score >= 80;
        String status = isCompliant ? "COMPLIANT" : "NON_COMPLIANT";

        // Save/Update in Database
        UserDevice device = deviceRepository.findById(dto.getDeviceId())
                .orElse(new UserDevice());

        device.setDeviceId(dto.getDeviceId());
        device.setUserId(dto.getUserId());
        device.setOsType(dto.getOsType());
        device.setOsVersion(dto.getOsVersion());

        // FIX: Correct Lombok Setters (No 'Is' in setter name)
        device.setFirewallOn(dto.isFirewallOn());
        device.setDiskEncrypted(dto.isDiskEncrypted());

        device.setStatus(status);
        device.setLastSeen(LocalDateTime.now());

        deviceRepository.save(device);

        return isCompliant;
    }

    // --- 2. Register Device (New User Sync) ---
    public void registerDevice(String deviceId, String userId) {
        if (!deviceRepository.existsById(deviceId)) {
            UserDevice newDevice = new UserDevice();
            newDevice.setDeviceId(deviceId);
            newDevice.setUserId(userId);
            newDevice.setStatus("COMPLIANT");
            newDevice.setLastSeen(LocalDateTime.now());

            // Set defaults
            newDevice.setOsType("Windows");
            newDevice.setOsVersion("11");

            // FIX: Correct Lombok Setters
            newDevice.setFirewallOn(true);
            newDevice.setDiskEncrypted(true);

            deviceRepository.save(newDevice);
            System.out.println("✅ Database: Inserted new device " + deviceId);
        } else {
            System.out.println("ℹ️ Database: Device already exists.");
        }
    }

    // --- 3. Check Compliance (Filter) ---
    public boolean isDeviceCompliant(String deviceId) {
        if (deviceId == null) return false;
        return deviceRepository.findById(deviceId)
                .map(device -> "COMPLIANT".equals(device.getStatus()))
                .orElse(false);
    }
}