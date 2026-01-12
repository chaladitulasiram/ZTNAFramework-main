package com.example.ztnaframework.model;


import lombok.Data;

@Data
public class DevicePostureDTO {
    private String deviceId;
    private String userId;
    private String osType;     // e.g., "Windows 11"
    private String osVersion;  // e.g., "10.0.22000"
    private boolean firewallEnabled;
    private boolean diskEncrypted;
    private boolean antivirusEnabled;
}
