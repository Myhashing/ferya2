package com.mlmfreya.ferya2.config;

import com.mlmfreya.ferya2.service.AuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implementsAuthenticationSuccessHandler {

    private final AuditService auditService;

    public CustomAuthenticationSuccessHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        String sessionId = request.getSession(false).getId();
        String ip = request.getRemoteAddr();
        auditService.recordLogin(email, sessionId, ip);
        response.sendRedirect("/dashboard");
    }
}