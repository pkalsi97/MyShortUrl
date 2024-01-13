package com.pk.MyShortUrl.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
// Used as an DTO to facilitate creation of custom urls
@NoArgsConstructor
@Getter
@Setter
public class CustomUrlRequest {
    private String originalUrl;
    private String backHalf;
}
