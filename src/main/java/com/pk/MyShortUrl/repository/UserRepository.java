package com.pk.MyShortUrl.repository;

import com.pk.MyShortUrl.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    User findByUsername(String username);

    boolean existsByEmail(String email);
}
