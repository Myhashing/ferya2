package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditRepository extends JpaRepository<Audit, Long> {
    Audit findBySessionId(String sessionId);

    Audit findByEmailAndSessionId(String email, String sessionId);

    List<Audit> findByEmail(String email);
}