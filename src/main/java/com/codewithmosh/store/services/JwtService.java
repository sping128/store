package com.codewithmosh.store.services;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXPIRY_MS = 15 * 60 * 1000; // 15 min
    private static final long REFRESH_TOKEN_EXPIRY_MS = 1 * 24 * 60 * 60 * 1000; // 1 day

    public static final String TOKEN_TYPE_REFRESH = "refresh";
    public static final String TOKEN_TYPE_ACCESS = "access";

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(String username) {
        return generateToken(username, ACCESS_TOKEN_EXPIRY_MS, TOKEN_TYPE_ACCESS);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, REFRESH_TOKEN_EXPIRY_MS, TOKEN_TYPE_REFRESH);
    }

    private String generateToken(String username, long durationInMs, String type) {
        var jwts = Jwts.builder();
        jwts.subject(username);
        jwts.claim("type", type);
        var now = new Date();
        jwts.issuedAt(now);
        jwts.expiration(new Date(System.currentTimeMillis() + durationInMs));
        jwts.signWith(this.secretKey);
        return jwts.compact();
    }

    public String extractUsername(String token) {
        var jwtsParser = Jwts.parser();
        jwtsParser.verifyWith(this.secretKey);
        var jwts = jwtsParser.build().parseSignedClaims(token);
        return jwts.getPayload().getSubject();
    }

    public String extractType(String token) {
        var jwtsParser = Jwts.parser();
        jwtsParser.verifyWith(this.secretKey);
        var jwts = jwtsParser.build().parseSignedClaims(token);
        return jwts.getPayload().get("type").toString();
    }
}
