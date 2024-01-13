package com.pk.MyShortUrl.service;

import com.pk.MyShortUrl.model.User;
import com.pk.MyShortUrl.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * annotates this calls is a service class
 * this is the actual implementation of interface JwtService
 * when a class implements an interface, it means that the class agrees to provide implementations for all the method
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor for UserDetailsServiceImpl, allowing dependency  injection
     * @param userRepository The repository handling user data.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // load the user who is autheticating
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) {
        // fetch user details
        User user = userRepository.findByUsername(username);
        // if not found throw exception
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        // Return a user details subject required by spring security, this includes username,password and role.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                // Assign user role of "USER"
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
    }
}
