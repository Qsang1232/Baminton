package com.example.demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${JWT_SECRET}")
    private String secretKey;

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration;

    private Key signInKey;

    // 🔹 Decode & validate JWT_SECRET đúng 1 lần khi app start
    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("❌ JWT_SECRET is missing or empty");
        }

        byte[] keyBytes;

        try {
            // Thử Base64 chuẩn trước
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (Exception e) {
            try {
                // Fallback sang Base64URL (_ và -)
                keyBytes = Decoders.BASE64URL.decode(secretKey);
            } catch (Exception ex) {
                throw new IllegalStateException(
                        "❌ JWT_SECRET is not valid Base64 or Base64URL", ex
                );
            }
        }

        // HS256 yêu cầu >= 256 bits = 32 bytes
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "❌ JWT_SECRET must be at least 256 bits (32 bytes)"
            );
        }

        this.signInKey = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("✅ JWT secret loaded, length = " + keyBytes.length);
    }

    // ================= JWT CORE =================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + jwtExpiration)
                )
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signInKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
