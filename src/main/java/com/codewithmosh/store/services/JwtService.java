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

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String username) {
        var jwts = Jwts.builder();
        jwts.subject(username);
        var now = new Date();
        jwts.issuedAt(now);
        jwts.expiration(new Date(now.getTime() + 24 * 60 * 60 * 1000));
        jwts.signWith(this.secretKey);
        return jwts.compact();
    }

    public String extractUsername(String token) {
        var jwtsParser = Jwts.parser();
        jwtsParser.verifyWith(this.secretKey);
        var jwts = jwtsParser.build().parseSignedClaims(token);
        return jwts.getPayload().getSubject();
    }
}
