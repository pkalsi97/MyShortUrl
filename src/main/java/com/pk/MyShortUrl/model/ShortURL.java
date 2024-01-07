package com.pk.MyShortUrl.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "shorturls")
@Data
@NoArgsConstructor
public class ShortURL {

    @Id
    private String id;
    private String originalUrl;
    private String shortLink;
    private String qrCode;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private boolean active;
    private String userId;

    public ShortURL(String originalUrl, String shortLink, String userId) {
        this.originalUrl = originalUrl;
        this.shortLink = shortLink;
        this.creationDate = LocalDateTime.now();
        this.expirationDate = this.creationDate.plusHours(48);
        this.active = true;
        this.userId = userId;
    }
}
