package com.pk.MyShortUrl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Used to transfer data about shortUrls
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortURLDto {
    private String originalUrl;
    private String shortLink;
    private String creationDate;
    private String expirationDate;
    private boolean active;
    private String userId;
    private int clickCount;
}
