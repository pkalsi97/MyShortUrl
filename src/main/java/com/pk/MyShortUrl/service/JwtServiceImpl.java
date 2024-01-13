package com.pk.MyShortUrl.service;

import io.jsonwebtoken.Claims; // Represents claim of a JWT
import io.jsonwebtoken.Jwts; // Provides methods for creating and parsing jwt
import io.jsonwebtoken.security.Keys; // generates cryptographic keys for jwt
import org.springframework.beans.factory.annotation.Value; // allows reading of values from application properties
import org.springframework.security.core.userdetails.UserDetails;// represents user details for authentication
import org.springframework.stereotype.Service; // indicates this class is a service component
import io.jsonwebtoken.io.Decoders; // utilities required to decode JWT

import javax.crypto.SecretKey; // Cryptographic security key
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// annotates this calls is a service class
// this is the actual implementation of interface JwtService
// when a class implements an interface, it means that the class agrees to provide implementations for all the methods declared in that interface.
@Service
public class JwtServiceImpl implements JwtService {
    // Get value of secret key from application properties
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    // set expiry time of token
    private static final long EXPIRATION_TIME = 3600000;
    // extract username from token
    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // generate token
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // check validity of token
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // extract cleans from token such as username
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    // generate token
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() +EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    // CHECK IF TOKEN IS EXPIRED
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiry date  from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // get secret key.
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
