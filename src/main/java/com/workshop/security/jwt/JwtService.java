package com.workshop.security.jwt;

import com.workshop.security.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY;
    private final long EXPIRATION_TIME;

    public JwtService(@Value("${jwt.secret_key}")String key, @Value("${jwt.expiration}")long exp_time){
        this.SECRET_KEY = key;
        this.EXPIRATION_TIME = exp_time;
    }

    // Generate Token
    public String generateToken(Authentication authentication){
        UserDetails userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extract Claims from token
    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }


    // extract a certain claim
    private <T> T extractClaim(String token, Function<Claims, T> resolver){
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }


    // extract username
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // extract expiration time
    public Date extractExpirationDate(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return extractExpirationDate(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        return !isTokenExpired(token)&& extractUsername(token).equals(userDetails.getUsername());
    }

}
