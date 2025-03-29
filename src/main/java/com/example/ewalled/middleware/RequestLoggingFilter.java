package com.example.ewalled.middleware;

import com.example.ewalled.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String traceId = UUID.randomUUID().toString();
        String userId = getAuthenticatedUserId();

        MDC.put("traceId", traceId);
        MDC.put("userId", userId);

        response.setHeader("X-Trace-Id", traceId);

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int status = response.getStatus();

            log.info("HTTP {} {} → {} ({} ms)",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    duration
            );

            MDC.clear();
        }
    }

    private String getAuthenticatedUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();

                if (principal instanceof User) {
                    return "" + ((User) principal).getId();
                }

                if (principal instanceof String) {
                    return (String) principal;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract user from security context", e);
        }

        return "anonymous";
    }
}

