package com.creativeshouse.gatewayservice.util;

import com.creativeshouse.gatewayservice.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    private final JwtConfig jwtConfig;
    private SecretKey accessKey;

    public JwtUtils(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @PostConstruct
    public void init() {
        this.accessKey = getKey(jwtConfig.getAccess().getSecret());
    }

    public boolean validateAccessToken(String token) {
        try {
            parseClaims(token, accessKey);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public String extractEmailFromAccessToken(String token) {
        return extractClaimFromAccessToken(token, Claims::getSubject);
    }

    public Long extractUserIdFromAccessToken(String token) {
        Claims claims = parseClaims(token, accessKey);
        return claims.get("userId", Long.class);
    }

    public java.util.List<String> extractRolesFromAccessToken(String token) {
        Claims claims = parseClaims(token, accessKey);
        return claims.get("roles", java.util.List.class);
    }

    public String getTokenId(String token) {
        // In user-service, is there a jti claim? The previously seen code didn't actually set 'jti' during generation.
        // If not set, user-service used the entire token string in Redis.
        // Let's implement getTokenId just in case it's added later or if user-service uses it now.
        // Actually, user-service used the whole token string:
        // redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, ...)
        // So getTokenId is just returning the token itself for simplicity if 'jti' is null, or matching user-service logic.
        return token;
    }

    private <T> T extractClaimFromAccessToken(String token, Function<Claims, T> resolver) {
        Claims claims = parseClaims(token, accessKey);
        return resolver.apply(claims);
    }

    private Claims parseClaims(String token, SecretKey key) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey(String secretKey) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
