package com.mlmfreya.ferya2.component;

import com.mlmfreya.ferya2.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Component
public class LogoutListener implements ApplicationListener<LogoutSuccessEvent> {

    @Autowired
    private AuditService auditService;

    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        // Retrieve the ip from the session
        String ip = (String) RequestContextHolder.currentRequestAttributes().getAttribute("ip", RequestAttributes.SCOPE_SESSION);

        auditService.recordLogout(email, sessionId, ip);
    }
}
