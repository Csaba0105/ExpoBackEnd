package com.example.springboot3jwtauthentication.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String clientIp = request.getRemoteAddr();

        // INFO szint - normál API hívások naplózása
        if ("GET".equalsIgnoreCase(method) || "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            log.info("Method: [{}], Endpoint: {}, From IP: {}", method, uri, clientIp);
        }

        // WARN szint - ha DELETE kérés érkezik (pl. adatvesztés veszélye miatt)
        if ("DELETE".equalsIgnoreCase(method)) {
            log.warn("Method: [{}], Endpoint: {}, From IP: {}", method, uri, clientIp);
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // ERROR szint - ha kivétel történik
            log.error("Hiba történt [{}] {} IP: {} - Error: {}", method, uri, clientIp, e.getMessage());
            throw e;
        }

        // Ha a válasz státuszkódja >= 400, akkor WARN vagy ERROR log
        int status = response.getStatus();
        if (status >= 400 && status < 500) {
            log.warn("HTTP {} - klienshiba [{}] {}", status, method, uri);
        } else if (status >= 500) {
            log.error("HTTP {} - szerverhiba [{}] {}", status, method, uri);
        }
    }
}
