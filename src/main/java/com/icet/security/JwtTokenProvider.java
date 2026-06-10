package com.icet.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Using a hardcoded secret for demonstration, should be in application.properties
    private final String jwtSecret = "af5b2cd01633512a818c4bd2ff00e5757d23d8c1e7a68e0d4a9d8f3c7b8e5c4a1b0d7f9e8c2a4b6d3f1e9c8a5b2d7e4f1c9a0b8d7e6f5c4b3a2d1e0f9c8b7a6";
    private final long jwtExpirationMs = 86400000; // 24 hours

    public String generateToken(String email, Long roleId, String roleName) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("roleId", roleId)
                .claim("roleName", roleName)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public String getRoleNameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("roleName", String.class);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith((javax.crypto.SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Invalid token
        }
        return false;
    }
}
