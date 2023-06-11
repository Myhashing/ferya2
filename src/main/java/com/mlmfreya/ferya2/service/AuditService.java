package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.model.Audit;
import com.mlmfreya.ferya2.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public void recordLogout(String email) {
        Audit audit = auditRepository.findByEmailAndSessionId(email, sessionId);
        if (audit != null) {
            audit.setLogoutTime(LocalDateTime.now());
            auditRepository.save(audit);
        }
    }


}
