package com.pk.MyShortUrl.repository;

import com.pk.MyShortUrl.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // check if username is taken
    boolean existsByUsername(String username);
    // find a user username
    User findByUsername(String username);
    // check if email exist
    boolean existsByEmail(String email);
}
