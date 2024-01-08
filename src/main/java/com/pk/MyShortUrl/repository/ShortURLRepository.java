package com.pk.MyShortUrl.repository;

import com.pk.MyShortUrl.model.ShortURL;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShortURLRepository extends MongoRepository<ShortURL, String> {
    List<ShortURL> findAllByUserId(String userId);
    Optional<ShortURL> findByShortLinkAndActive(String shortLink, boolean active);
    Optional<ShortURL> findByShortLink(String shortLink);
    int countByUserIdAndActive(String userId, boolean active);

    List<ShortURL> findAllByUserIdAndActive(String userId, boolean active);
    Optional<ShortURL> findByIdAndUserId(String id, String userId);

    List<ShortURL> findByActiveTrueAndExpirationDateBetween(Date start, Date end);


}



