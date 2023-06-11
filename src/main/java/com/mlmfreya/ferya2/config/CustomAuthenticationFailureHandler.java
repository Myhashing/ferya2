package com.mlmfreya.ferya2.config;

import com.mlmfreya.ferya2.service.AuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final AuditService auditService;

    public CustomAuthenticationFailureHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        String sessionId = request.getSession(false).getId();
        String ip = request.getRemoteAddr();
        auditService.recordLoginFailure(email, sessionId, ip);
        response.sendRedirect("/login?error=true");
    }
}
