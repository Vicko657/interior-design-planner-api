package com.interiordesignplanner.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * JwtService (JSON Web Token)
 * 
 * Generates, decodes and validates the JwtToken
 */
@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.time}")
    private Long expiration;

    /**
     * Generates the JwtToken
     * 
     * With expiration time & secret key
     * 
     */
    public String generateJwtToken(ApplicationUserDetails applicationUserDetails) {

        String jwt = "";

        jwt = Jwts.builder().subject(
                applicationUserDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();

        return jwt;

    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, ApplicationUserDetails applicationUserDetails) {

        String username = extractUsername(token);
        return (username.equals(applicationUserDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();

        return claimsResolver.apply(claims);
    }
}