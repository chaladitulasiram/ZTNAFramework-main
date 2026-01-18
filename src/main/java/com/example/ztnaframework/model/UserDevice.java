package com.example.ztnaframework.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID; // Import UUID

@Entity
@Table(name = "user_devices")
@Data
public class UserDevice {
    @Id
    @Column(name = "device_id")
    private String deviceId;

    // CHANGED: String -> UUID to match Database Type
    @Column(name = "user_id")
    private UUID userId;

    private String osType;
    private String osVersion;

    private boolean isFirewallOn;
    private boolean isDiskEncrypted;

    private String status;
    private LocalDateTime lastSeen;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastSeen = LocalDateTime.now();
    }
}