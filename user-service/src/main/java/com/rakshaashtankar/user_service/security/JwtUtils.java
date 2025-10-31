package com.rakshaashtankar.user_service.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private final String jwtSecret = "mySuperSecretKeyThatIsDefinitelyLongEnoughToBeAtLeastSixtyFourCharactersLong!!!123456"; // min 32 chars for HS512
    private final long jwtExpirationMs = 3600000; //1 hr


    //Convert string secret to Key object
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    //Generate token
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)       //set who is token is for
                .claim("role", role)     //add extra info, here the role
                .setIssuedAt(new Date())    //token creation time
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))  //expiry
                .signWith(getSigningKey(),SignatureAlgorithm.HS512)  //sign with secret
                .compact(); //convert to string

    }

    //Extract username
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //Extract role
    public String getRoleFromToken(String token) {
        return (String) Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    //Validate token
    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
