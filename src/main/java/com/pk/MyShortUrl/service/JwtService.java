package com.pk.MyShortUrl.service;

import org.springframework.security.core.userdetails.UserDetails;

// this is used to extract username , generate token and validate tokens
public interface JwtService {
    String extractUserName(String token);
    String generateToken(UserDetails userDetails);
    boolean isTokenValid(String token, UserDetails userDetails);
}
