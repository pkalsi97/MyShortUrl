package com.pk.MyShortUrl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data               // Generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor  // Generates a no-argument constructor
@AllArgsConstructor // Generates an all-argument constructor
public class ShortURLDto {
    private String originalUrl;
    private String shortLink;
    private String creationDate;
    private String expirationDate;
    private boolean active;
    private String userId;
    private int clickCount;
}
