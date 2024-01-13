package com.pk.MyShortUrl.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
// used as an DTO to return the Jwt token generated
@Getter
@RequiredArgsConstructor
public class AuthenticationResponse {
    private final String jwt;
}
