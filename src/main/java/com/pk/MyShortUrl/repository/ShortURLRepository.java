package com.pk.MyShortUrl.repository;

import com.pk.MyShortUrl.model.ShortURL;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
// annotates this is being used by spring repository
@Repository
public interface ShortURLRepository extends MongoRepository<ShortURL, String> {
    // find all short Urls associated with a user using userId
    List<ShortURL> findAllByUserId(String userId);
    // Find all short url using short link
    Optional<ShortURL> findByShortLink(String shortLink);

    // Method to count the number of ShortURLs owned by a user that are active
    int countByUserIdAndActive(String userId, boolean active);

    // Method to find all ShortURLs owned by a user that are active
    List<ShortURL> findAllByUserIdAndActive(String userId, boolean active);

    // Method to find all active ShortURLs with expiration dates within a specified range
    List<ShortURL> findByActiveTrueAndExpirationDateBetween(Date start, Date end);

    // Method to check if a provided URL is already a short URL in the database
    boolean existsByShortLink(String shortLink);
}



