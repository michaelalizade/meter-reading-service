package com.utilread.meterreading.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenValidator jwtTokenValidator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        boolean requiresAuth = handlerMethod.hasMethodAnnotation(Authenticated.class)
                || handlerMethod.getBeanType().isAnnotationPresent(Authenticated.class);

        if (!requiresAuth) {
            log.debug("Endpoint does not require authentication: {}", request.getRequestURI());
            return true;
        }

        log.debug("Endpoint requires authentication: {}", request.getRequestURI());

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            log.warn("Missing Authorization header for protected endpoint: {}", request.getRequestURI());
            sendUnauthorizedResponse(response, "Missing authorization token");
            return false;
        }

        try {
            UserPrincipal principal = jwtTokenValidator.validateToken(authHeader);

            SecurityContext.setCurrentUser(principal);

            log.debug("User authenticated: {}", principal.getEmail());
            return true;

        } catch (SecurityException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            sendUnauthorizedResponse(response, "Invalid token: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContext.clear();
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        try {
            response.getWriter().write("{\"error\":\"" + message + "\"}");
        } catch (Exception e) {
            log.error("Error writing unauthorized response", e);
        }
    }
}