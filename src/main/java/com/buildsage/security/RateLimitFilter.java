package com.buildsage.security;

import com.buildsage.api.ApiError;
import com.buildsage.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String key = request.getRemoteAddr() + ":" + request.getRequestURI();
        Window window = windows.compute(
                key,
                (ignored, existing) -> existing == null || existing.expired() ? new Window() : existing.increment());
        if (window.count > 120) {
            response.setStatus(429);
            response.setContentType("application/json");
            objectMapper.writeValue(
                    response.getOutputStream(), ApiResponse.fail(ApiError.of("RATE_LIMITED", "Too many requests")));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static final class Window {
        private final Instant start = Instant.now();
        private int count = 1;

        private boolean expired() {
            return start.plusSeconds(60).isBefore(Instant.now());
        }

        private Window increment() {
            count++;
            return this;
        }
    }
}
