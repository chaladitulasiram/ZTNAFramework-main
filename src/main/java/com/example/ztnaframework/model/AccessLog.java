package com.example.ztnaframework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String userEmail;
    private LocalDateTime timestamp;
    private String endpoint;
    private String method;
    private String ipAddress;
    private String deviceId;
    private int statusCode;
    private int riskScore;
    private String outcome;
}