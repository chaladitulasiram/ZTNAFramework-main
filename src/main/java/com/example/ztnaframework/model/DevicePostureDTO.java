package com.example.ztnaframework.model;

import lombok.Data;

@Data
public class DevicePostureDTO {
    private String deviceId;
    private String userId;
    private String osType;     // e.g., "Windows 11"
    private String osVersion;  // e.g., "10.0.22000"

    // RENAMED to match UserDevice Entity
    private boolean isFirewallOn;
    private boolean isDiskEncrypted;

    private boolean antivirusEnabled;
}