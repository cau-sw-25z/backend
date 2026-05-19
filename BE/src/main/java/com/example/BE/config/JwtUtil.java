package com.example.BE.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 비밀키
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    // 만료 시간
    private final long ACCESS_TOKEN_EXPIRATION  = 1000 * 60 * 30;           // 30분
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;  // 7일

    // Access Token 생성
    public String generateAccessToken(Long userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(Long userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    // 토큰에서 이메일 꺼내기
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰에서 userId 꺼내기
    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 파싱 (내부용)
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}