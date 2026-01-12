package com.example.ztnaframework.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_devices")
@Data
public class UserDevice {
    @Id
    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "user_id")
    private String userId;

    private String osType;
    private String osVersion;
    private boolean isFirewallOn;
    private boolean isDiskEncrypted;

    private String status; // 'COMPLIANT', 'NON_COMPLIANT'
    private LocalDateTime lastSeen;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastSeen = LocalDateTime.now();
    }
}