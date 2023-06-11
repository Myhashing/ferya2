package com.mlmfreya.ferya2.component;

import com.mlmfreya.ferya2.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Aspect
@Component
public class AuditAspect {
    @Autowired
    private AuditService auditService;

    @After("execution(* org.springframework.security.authentication.AuthenticationManager.authenticate(..))")
    public void logLogin(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        String email = authentication.getName();
        String ipAddress = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        auditService.recordLogin(email, sessionId, ipAddress);

        // Store the email and ip in the session
        RequestContextHolder.currentRequestAttributes().setAttribute("email", email, RequestAttributes.SCOPE_SESSION);
        RequestContextHolder.currentRequestAttributes().setAttribute("ip", ipAddress, RequestAttributes.SCOPE_SESSION);
    }

    @After("execution(* org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler.logout(..))")
    public void logLogout(JoinPoint joinPoint) {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        // Retrieve the email and ip from the session
        String email = (String) RequestContextHolder.currentRequestAttributes().getAttribute("email", RequestAttributes.SCOPE_SESSION);
        String ip = (String) RequestContextHolder.currentRequestAttributes().getAttribute("ip", RequestAttributes.SCOPE_SESSION);

        auditService.recordLogout(email, sessionId, ip);
    }
}

