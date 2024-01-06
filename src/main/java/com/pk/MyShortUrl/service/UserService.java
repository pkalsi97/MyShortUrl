package com.pk.MyShortUrl.service;

import com.pk.MyShortUrl.model.User;
import com.pk.MyShortUrl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean checkUserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User registerUser(String username, String email, String password) {
        if (!checkUserExists(username)) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email); // Set email
            newUser.setPassword(passwordEncoder.encode(password)); // Encode the password
            newUser.setUrlLimit(5);
            userRepository.save(newUser);
            return newUser;
        }
        return null;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean loginUser(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true; // Password matches
        }
        return false; // User not found or password does not match
    }
}
