package com.creativeshouse.post_service.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HeaderAuthFilter extends OncePerRequestFilter {

    @Value("${internal.secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Verify internal secret
        String providedSecret = request.getHeader("X-Internal-Secret");
        if (providedSecret == null || !providedSecret.equals(internalSecret)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Invalid internal secret");
            return;
        }

        // 2. Read user identity headers
        String userIdStr = request.getHeader("X-User-Id");
        String rolesStr = request.getHeader("X-User-Roles");

        if (userIdStr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Long userId = Long.parseLong(userIdStr);
            
            // Reconstruct minimal authentication context for Post Service from Roles
            List<SimpleGrantedAuthority> authorities = java.util.Collections.emptyList();
            if (rolesStr != null && !rolesStr.isEmpty()) {
                authorities = Arrays.stream(rolesStr.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute("userId", userId);
        }

        filterChain.doFilter(request, response);
    }
}
