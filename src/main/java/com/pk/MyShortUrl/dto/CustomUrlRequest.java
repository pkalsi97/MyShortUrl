package com.pk.MyShortUrl.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Setter
public class CustomUrlRequest {
    private String originalUrl;
    private String backHalf;
}
