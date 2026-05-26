package com.finchtalk.smart_learning_application.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtConfig(
        String issuer,
        String secret,
        long expirationSeconds
) {
}

