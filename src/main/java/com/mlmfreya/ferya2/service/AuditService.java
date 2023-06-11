package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.Audit;
import com.mlmfreya.ferya2.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {
    @Autowired
    private AuditRepository auditRepository;

    public void recordLogin(String email, String sessionId, String ipAddress) {
        Audit audit = new Audit();
        audit.setEmail(email);
        audit.setSessionId(sessionId);
        audit.setIpAddress(ipAddress);
        audit.setLoginTime(LocalDateTime.now());
        auditRepository.save(audit);
    }

    public void recordLogout(String email, String sessionId, String ip) {
        Audit audit = auditRepository.findByEmailAndSessionId(email, sessionId);
        if (audit != null) {
            audit.setLogoutTime(LocalDateTime.now());
            auditRepository.save(audit);
        }
    }

    public void recordLoginFailure(String email, String sessionId, String ip) {
        Audit event = new Audit();
        event.setEmail(email);
        event.setSessionId(sessionId);
        event.setIpAddress(ip);
        event.setEventType("LOGIN_FAILURE");
        event.setEventTimestamp(LocalDateTime.now());
        auditRepository.save(event);
    }

    public void recordLoginSuccess(String email, String sessionId, String ip) {
        Audit event = new Audit();
        event.setEmail(email);
        event.setSessionId(sessionId);
        event.setIpAddress(ip);
        event.setEventType("LOGIN_SUCCESS");
        event.setEventTimestamp(LocalDateTime.now());
        auditRepository.save(event);
    }
    public List<Audit> getAuditsForUser(String email) {
        return auditRepository.findByEmail(email);
    }





}
