package com.mlmfreya.ferya2.component;

import com.mlmfreya.ferya2.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @AfterReturning(
            pointcut="execution(* org.springframework.security.authentication.AuthenticationManager.authenticate(..))",
            returning="authentication")
    public void logLogin(JoinPoint joinPoint, Authentication authentication) {
        if (authentication != null) {
            String name = authentication.getName();
            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
            String email = authentication.getName();
            String ipAddress = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
            auditService.recordLogin(email, sessionId, ipAddress);

            // Store the email and ip in the session
            RequestContextHolder.currentRequestAttributes().setAttribute("email", email, RequestAttributes.SCOPE_SESSION);
            RequestContextHolder.currentRequestAttributes().setAttribute("ip", ipAddress, RequestAttributes.SCOPE_SESSION);
        } else {
            // handle the case where authentication is null
            // Log the authentication failure
            logger.warn("Authentication failed or not yet initiated");
        }
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

