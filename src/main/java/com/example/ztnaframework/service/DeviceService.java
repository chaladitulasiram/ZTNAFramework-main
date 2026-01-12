package com.example.ztnaframework.service;

import com.example.ztnaframework.model.DevicePostureDTO;
import com.example.ztnaframework.model.UserDevice;
import com.example.ztnaframework.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }
    @Transactional

    public boolean evaluatePosture(DevicePostureDTO dto) {
        int score = 0;
        if (dto.getOsType() != null) score += 20;
        if (dto.isFirewallEnabled()) score += 30;
        if (dto.isDiskEncrypted()) score += 30;
        if (dto.isAntivirusEnabled()) score += 20;

        boolean isCompliant = score >= 80;
        String status = isCompliant ? "COMPLIANT" : "NON_COMPLIANT";

        // SAVE TO SUPABASE VIA JDBC
        UserDevice device = new UserDevice();
        device.setDeviceId(dto.getDeviceId());
        device.setUserId(dto.getUserId());
        device.setOsType(dto.getOsType());
        device.setOsVersion(dto.getOsVersion());
        device.setFirewallOn(dto.isFirewallEnabled());
        device.setDiskEncrypted(dto.isDiskEncrypted());
        device.setStatus(status);

        deviceRepository.save(device); // <--- This writes to DB

        return isCompliant;
    }

    public boolean isDeviceCompliant(String deviceId) {
        if (deviceId == null) return false;

        // READ FROM SUPABASE VIA JDBC
        Optional<UserDevice> deviceOpt = deviceRepository.findById(deviceId);

        return deviceOpt.map(device -> "COMPLIANT".equals(device.getStatus()))
                .orElse(false);
    }
}