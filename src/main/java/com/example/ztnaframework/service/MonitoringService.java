package com.example.ztnaframework.service;

import com.example.ztnaframework.model.AccessLog;
import com.example.ztnaframework.repository.AccessLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MonitoringService {

    private final AccessLogRepository logRepository;

    public MonitoringService(AccessLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Async
    public void logEvent(AccessLog log) {
        // Calculate Risk (Simple Logic)
        int risk = 0;
        if (log.getStatusCode() == 403) risk += 40;
        if (log.getEndpoint().contains("/admin")) risk += 20;

        log.setRiskScore(risk);

        // SAVE TO SUPABASE
        logRepository.save(log);

        if (risk > 50) {
            System.err.println("HIGH RISK EVENT SAVED TO DB: " + log.getId());
        }
    }
}