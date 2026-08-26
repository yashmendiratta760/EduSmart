package com.yash.EduSmart.config;

import com.yash.RateLimiter.config.RateLimitKeyResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class EduSmartRateLimitKeyResolver implements RateLimitKeyResolver {

    @Override
    public String resolve(HttpServletRequest request) {

        String email = "anonymous";

        if (request.getUserPrincipal() != null) {
            email = request.getUserPrincipal().getName();
        }

        String ip = request.getRemoteAddr();

        return email + ":" + ip;
    }
}