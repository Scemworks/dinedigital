package com.dinedigital.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class JwtService {
    private final Key key;

    public JwtService(@Value("${security.jwt.secret:}") String secret) {
        // Accept raw or base64 secrets; if missing/weak, generate a secure HS256 key (dev-safe fallback)
        Key k;
        try {
            byte[] keyBytes = null;
            if (secret != null && !secret.isBlank()) {
                if (secret.startsWith("base64:")) {
                    keyBytes = Base64.getDecoder().decode(secret.substring(7));
                } else {
                    keyBytes = secret.getBytes(StandardCharsets.UTF_8);
                }
            }
            if (keyBytes == null || keyBytes.length < 32) {
                // Fallback: generate strong key (tokens won't persist across restarts). Configure a 32+ byte secret for stable tokens.
                k = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            } else {
                k = Keys.hmacShaKeyFor(keyBytes);
            }
        } catch (Exception e) {
            k = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
        this.key = k;
    }

    public String generateToken(String username, String role, long ttlMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("role", role))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public io.jsonwebtoken.Claims validate(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
