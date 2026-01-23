package com.example.demo.service;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtService {

    @Value("${JWT_SECRET}")
    private String secretKey;

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration;

    private Key signInKey;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes;

            // Ưu tiên Base64 chuẩn
            try {
                keyBytes = Decoders.BASE64.decode(secretKey);
            } catch (Exception ex) {
                // Fallback Base64URL (dùng khi key có _ hoặc -)
                keyBytes = Decoders.BASE64URL.decode(secretKey);
            }

            if (keyBytes.length < 32) {
                throw new IllegalStateException("❌ JWT_SECRET must be at least 256 bits (32 bytes)");
            }

            this.signInKey = Keys.hmacShaKeyFor(keyBytes);
            System.out.println("✅ JWT secret loaded successfully, length = " + keyBytes.length);

        } catch (Exception e) {
            throw new IllegalStateException("❌ JWT_SECRET is not valid Base64/Base64URL", e);
        }
    }

    public Key getSignInKey() {
        return signInKey;
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }
}
