package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    private final long jwtExpirationMs = 24 * 60 * 60 * 1000;
    public String genrateToken(String username){
        return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis()+jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
    }
    public String getUsernameFromToken(String token){
        return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token);
            return true;
        }catch(Exception ex){
            return false;
        }
    }
    public Date getExpirationDateFromToken(String token){
        return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody()
        .getExpiration();
    }
}