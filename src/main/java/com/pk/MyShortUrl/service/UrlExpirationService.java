package com.pk.MyShortUrl.service;

import com.pk.MyShortUrl.model.ShortURL;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UrlExpirationService {

    private final MongoTemplate mongoTemplate; // inject the mongo template to interact with mongo db
    //run every 10 seconds
    @Scheduled(fixedRate = 30000)
    public void deactivateExpiredUrls() {
        // Get the current time in Asia/Kolkata time zone
        LocalDateTime nowInIst = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        Date startDate = Date.from(nowInIst.atZone(ZoneId.of("Asia/Kolkata")).toInstant());
        Date endDate = Date.from(nowInIst.plusMinutes(1).atZone(ZoneId.of("Asia/Kolkata")).toInstant());
        // Create a query to find all active ShortURLs where the expiration date is within the next minute
        Query query = new Query(Criteria.where("active").is(true).and("expirationDate").gte(startDate).lte(endDate));
        // Define an update operation to set 'active' field to false
        Update update = new Update().set("active", false);
        // Execute the update operation on all documents matching the query
        mongoTemplate.updateMulti(query, update, ShortURL.class);
    }
}
