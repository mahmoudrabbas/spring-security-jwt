package com.workshop.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY;

    public JwtService(@Value("${SECRET_KEY}") String secretKey) {
        SECRET_KEY = secretKey;
    }

    // this method generate token with some claims inside
    public String /* key */ generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*10))
                .signWith(SignatureAlgorithm.ES256, SECRET_KEY)
                .compact();
    }


    // extract all claims from token
    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJwt(token).getBody();
    }


    // here I am extracting a certain claim like getSubject or getIssuedAt or even getExpiration
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // here I get claim ( getSubject ) which keeps the username inside
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }


    // checking if the token is expired or not yet
    private boolean isTokenExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }


    // checking if token is valid or not
    public boolean isTokenValid(String token, UserDetails userDetails){
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }



}
