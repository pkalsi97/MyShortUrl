package com.pk.MyShortUrl.controller;

import com.pk.MyShortUrl.dto.AuthenticationRequest;
import com.pk.MyShortUrl.dto.AuthenticationResponse;
import com.pk.MyShortUrl.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// Restful controller for authetication, provides an endpoint for user authetication
@RestController
public class AuthenticationController {
    // injecting the authentication manager to authenticate users details
    private final AuthenticationManager authenticationManager;
    // injecting user details to get user specific data
    private final UserDetailsService userDetailsService;
    // injecting jwt to handel user validate tokens
    private final JwtService jwtService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserDetailsService userDetailsService,
                                    JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }
    /**
     * Endpoint to authenticate a user.This method is called when a POST request is made to '/authenticate'.
     * It processes the authentication request and generates a JWT upon successful authentication.
     * @param authenticationRequest Contains the username and password submitted by the user.
     * @return ResponseEntity containing the JWT or an error message in case of failure.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            // uses spring security's built in mechanism to validate users
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // unable to authenticate
            throw new UsernameNotFoundException("Incorrect username or password");
        }
        // it the authentication is successful load the details of the iser
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        // generate JWT token
        final String jwt = jwtService.generateToken(userDetails);
        // Return the JWT wrapped in an AuthenticationResponse object as the response body.
        // Response entity can also be used to display errors, HTTP status etc
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
