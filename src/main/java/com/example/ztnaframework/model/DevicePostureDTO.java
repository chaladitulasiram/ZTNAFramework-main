package com.example.ztnaframework.model;

import lombok.Data;

@Data
public class DevicePostureDTO {
    private String deviceId;
    private String userId; // Keep as String here, converted in Service
    private String osType;
    private String osVersion;
    private boolean isFirewallOn;
    private boolean isDiskEncrypted;
    private boolean antivirusEnabled;
}