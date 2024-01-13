package com.pk.MyShortUrl.config;

import com.pk.MyShortUrl.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Mark this class as a Configuration class
@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtRequestFilter jwtRequestFilter;

    // automatically injects required dependencies
    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }
    // Bean indicates that this method will be managed by the spring container
    // here we define bean responsible for configuring security filters and urls for all urls
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Authorise authentication for HTTP request
                .authorizeHttpRequests(auth -> auth
                        // if matches these -> permit them without login else request must be authenticated
                        .requestMatchers("/register", "/login", "/logout", "/public/**", "/{shortLink}").permitAll()
                        .anyRequest().authenticated()
                )
                // configure form based login
                .formLogin(formLogin -> formLogin
                        // Defines the login page
                        .loginPage("/login").permitAll()
                        // if log in is true -> redirect to this page
                        .defaultSuccessUrl("/dashboard", true)
                        //if failure
                        .failureUrl("/login?error=true")
                )
                // configure logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/login").permitAll()
                )
                // configure session management
                .sessionManagement(sessionManagement -> sessionManagement
                        // session will only be created if user logs in
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // csrf is enabled for all request expect Restfull request.
                .csrf(csrf ->
                        csrf
                                .ignoringRequestMatchers("/api/url/**", "/authenticate")
                )
                // specify user details used for authentication
                .userDetailsService(userDetailsService)
                // both authentication mechanism are available for the application
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // build and return this -> this configuration will be used for authentication
        return http.build();
    }

    // bean responsible for encoding and verifying passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // bean used for authenticating users
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
