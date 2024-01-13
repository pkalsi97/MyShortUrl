package com.pk.MyShortUrl.config;

import com.pk.MyShortUrl.exception.InvalidTokenException;
import com.pk.MyShortUrl.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


// Declared as a Configuration class
// this class extends java class OncePerRequestFilter
@Configuration
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Autowired ->  is a way to tell Spring to automatically provide instances of UserDetailsService
     * and JwtService when creating an instance of JwtRequestFilter.
     */
    @Autowired
    public JwtRequestFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }
    // Override indicates that a method is subclass is intended to override a method with sameness in its superclass
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Retrieve the Authorization header from HTTP request
        final String authorizationHeader = request.getHeader("Authorization");

        //Initialise the variables to store username and JSON web token
        String username = null;
        String jwt = null;

        // check if Authorization header is not null and starts with  Bearer
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // remove the Bearer form the token
            jwt = authorizationHeader.substring(7);
            try {
                // try to get the username from the decoding of token (jwt variable)
                username = jwtService.extractUserName(jwt);
            } catch (Exception e) {
                // if unable to throw exception
                throw new InvalidTokenException("The token is invalid.");
            }
        }

        // here once we are able to get the  username from token we check if its not null and ensure
        // there is no existing authetication in security context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // get the user details with the help of username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // check validity of token by verifying it against username
            if (!jwtService.isTokenValid(jwt, userDetails)) {
                // If the token is not valid, throw the InvalidTokenException
                throw new InvalidTokenException("The token is invalid.");
            }

            // If the token is valid, proceed with setting the authentication in the security context
            // Create an instance of UsernamePasswordAuthenticationToken with user details and no password
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        // Continue with the filter chain, allowing the request to be processed further
        chain.doFilter(request, response);
    }

}
