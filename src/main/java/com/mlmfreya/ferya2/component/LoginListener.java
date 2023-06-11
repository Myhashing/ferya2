package com.mlmfreya.ferya2.component;

import com.mlmfreya.ferya2.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    private AuditService auditService;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        String ip = ((WebAuthenticationDetails) event.getAuthentication().getDetails()).getRemoteAddress();
        auditService.recordLogin(email, sessionId, ip);
    }
}
