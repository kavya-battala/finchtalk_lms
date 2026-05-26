package com.finchtalk.smart_learning_application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;

@Component
public class JwtTokenProvider {


    private final JwtConfig jwtConfig;

    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    private Key signingKey() {
        // secret must be sufficiently long for HS256
        byte[] keyBytes = jwtConfig.secret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String subject, String role) {

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtConfig.expirationSeconds());

        return Jwts.builder()
                .setIssuer(jwtConfig.issuer())
                .setSubject(subject)
                .claim("role", role)

                .setIssuedAt(java.util.Date.from(now))
                .setExpiration(java.util.Date.from(exp))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

