package com.qltnb.security;

import com.qltnb.entity.VaiTro;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // A secure 256-bit key for HMAC-SHA256 signature
    private final String jwtSecret = "qltl_luat_dan_su_secret_key_2026_spring_security_jwt_token_256bits_minimum";
    private final long jwtExpirationInMs = 604800000; // 7 days expiration

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String username, VaiTro role) {
        String roleName = role != null ? role.getVT_ten() : "NHAN_VIEN";
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", roleName);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Bad/expired token
        }
        return false;
    }
}
