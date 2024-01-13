package com.pk.MyShortUrl.service;

import com.pk.MyShortUrl.model.User;
import com.pk.MyShortUrl.repository.ShortURLRepository;
import com.pk.MyShortUrl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ShortURLRepository shortURLRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // CHECK IF USERNAME IS TAKEN
    public boolean checkUserExists(String username) {
        return userRepository.existsByUsername(username);
    }
    // Register user
    public User registerUser(String username, String email, String password) {
        if (!checkUserExists(username)) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setUrlLimit(5);
            userRepository.save(newUser);
            return newUser;
        }
        return null;
    }

    // Get active url cont for a user
    public int getActiveURLCount(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return shortURLRepository.countByUserIdAndActive(user.getId(), true);
        }
        return 0;
    }

    // get url limit for a user
    public int getUrlLimit(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getUrlLimit();
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
    // check email address is available
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // increment count of active urls
    @Transactional
    public void incrementActiveURLCount(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setActiveUrlCount(user.getActiveUrlCount() + 1);
            userRepository.save(user);
        }
    }
    // decrement count of active urls
    @Transactional
    public void decrementActiveURLCount(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getActiveUrlCount() > 0) {
            user.setActiveUrlCount(user.getActiveUrlCount() - 1);
            userRepository.save(user);
        }
    }

}