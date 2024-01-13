package com.pk.MyShortUrl.dto;

import lombok.Getter;
import lombok.Setter;
// used as a DTO to facilitate the disabling of an URL
@Getter
@Setter
public class DisableUrlRequest {
    private String shortUrl;
}