package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "aS9lK2dj3Nq9Xx8G5fQwzC6rV7tLb1PzJ8tR2K5xG3cB4tUvQ5mLfWp7"; // 최소 256비트
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 토큰 생성
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // SecretKey 객체 사용
                .compact();
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // SecretKey 객체 사용
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false; // 토큰이 유효하지 않은 경우
        }
    }

    /**
     * 토큰에서 사용자 이름 추출
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // SecretKey 객체 사용
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
