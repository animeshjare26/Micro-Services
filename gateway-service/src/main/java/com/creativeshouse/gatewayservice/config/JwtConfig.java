package com.creativeshouse.gatewayservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {

    private TokenConfig access;
    private TokenConfig refresh;

    @Data
    public static class TokenConfig {
        private String secret;
        private Long expiration;
    }
}
