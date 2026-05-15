package com.creativeshouse.gatewayservice.filter;

import com.creativeshouse.gatewayservice.service.TokenBlacklistService;
import com.creativeshouse.gatewayservice.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${internal.secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Allow public endpoints
        if (path.startsWith("/api/auth/signin") || 
            path.startsWith("/api/auth/signup") || 
            path.startsWith("/api/auth/token") || 
            path.startsWith("/eureka")) {
            // Inject the internal secret even for public routes so downstream can accept it
            HeaderMutatingRequest mutatingRequest = new HeaderMutatingRequest(request);
            mutatingRequest.putHeader("X-Internal-Secret", internalSecret);
            filterChain.doFilter(mutatingRequest, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);
        String tokenId = jwtUtils.getTokenId(token);

        if (tokenBlacklistService.isTokenBlacklisted(tokenId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has been revoked");
            return;
        }

        if (!jwtUtils.validateAccessToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid access token");
            return;
        }

        String email = jwtUtils.extractEmailFromAccessToken(token);
        Long userId = jwtUtils.extractUserIdFromAccessToken(token);
        List<String> roles = jwtUtils.extractRolesFromAccessToken(token);

        HeaderMutatingRequest mutatingRequest = new HeaderMutatingRequest(request);
        mutatingRequest.putHeader("X-User-Id", String.valueOf(userId));
        mutatingRequest.putHeader("X-User-Email", email);
        if (roles != null) {
            mutatingRequest.putHeader("X-User-Roles", String.join(",", roles));
        }
        mutatingRequest.putHeader("X-Internal-Secret", internalSecret);

        filterChain.doFilter(mutatingRequest, response);
    }
}
