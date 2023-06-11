package com.mlmfreya.ferya2.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    private final String sessionId;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.sessionId = request.getSession().getId();
    }

    public String getSessionId() {
        return sessionId;
    }
}
