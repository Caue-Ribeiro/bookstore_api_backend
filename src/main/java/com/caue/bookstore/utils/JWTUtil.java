package com.caue.bookstore.utils;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JWTUtil {
    Dotenv dotenv = Dotenv.load();
    private final String SECRET_KEY = dotenv.get("JWT_SECRET_KEY");
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


    public String generateToken(String username) {
        long EXPIRATION = 1000 * 60 * 60;
        return Jwts.builder().subject(username).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + EXPIRATION)).signWith(key).compact();
    }

    public String extractUsername(String token) {

        Claims body = extractClaims(token);

        return body.getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String username, UserDetails userDetails, String token) {

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public long getRemainingValiditySeconds(String token) {
        long remainingMillis = extractExpiration(token).getTime() - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            return 0;
        }
        return TimeUnit.MILLISECONDS.toSeconds(remainingMillis);
    }
}
