package com.mlmfreya.ferya2.component;

import com.mlmfreya.ferya2.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    private AuditService auditService;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        String sessionId = event.getAuthentication().getDetails().getSessionId();
        String ipAddress = event.getAuthentication().getDetails().getRemoteAddress();
        auditService.recordLogin(email, sessionId, ipAddress);
    }
}
