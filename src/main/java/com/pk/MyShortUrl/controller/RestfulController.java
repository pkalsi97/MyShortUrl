package com.pk.MyShortUrl.controller;

import com.pk.MyShortUrl.dto.CustomUrlRequest;
import com.pk.MyShortUrl.dto.ShortURLDto;
import com.pk.MyShortUrl.dto.ShortUrlRequest;
import com.pk.MyShortUrl.model.ShortURL;
import com.pk.MyShortUrl.service.ShortURLService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/url")
public class RestfulController {

    private final ShortURLService shortURLService;



    @Autowired
    public RestfulController(ShortURLService shortURLService) {
        this.shortURLService = shortURLService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<ShortURLDto>> getActiveURLsForUser(Principal principal) {

        String userId = principal.getName();
        List<ShortURL> activeURLs = shortURLService.getActiveShortURLsByUser(userId);
        // Convert each ShortURL to a ShortURLDto
        List<ShortURLDto> activeUrlsDto = activeURLs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(activeUrlsDto);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<ShortURLDto>> getInactiveURLsForUser(Principal principal) {
        String userId = principal.getName();
        List<ShortURL> allURLs = shortURLService.getAllShortURLsByUser(userId);

        List<ShortURLDto> inactiveUrlsDto = allURLs.stream()
                .filter(shortURL -> !shortURL.isActive()) // Filter for inactive URLs
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(inactiveUrlsDto);
    }

    @PostMapping("/generateRandom")
    public ResponseEntity<ShortURLDto> generateRandomShortUrl(@RequestBody ShortUrlRequest request, Principal principal) {
        String userId = principal.getName(); // Assuming the user's identity is determined by the Principal
        String backHalf = shortURLService.generateUniqueBackHalf();
        ShortURL shortURL = shortURLService.createShortURL(request.getOriginalUrl(), backHalf, userId);

        if (shortURL != null) {
            return ResponseEntity.ok(convertToDto(shortURL));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/generateCustom")
    public ResponseEntity<?> generateCustomShortUrl(@RequestBody CustomUrlRequest request, Principal principal) {
        String userId = principal.getName(); // Assuming the user's identity is determined by the Principal

        try {
            ShortURL shortURL = shortURLService.createShortURL(request.getOriginalUrl(), request.getBackHalf(), userId);
            return ResponseEntity.ok(convertToDto(shortURL));
        } catch (IllegalArgumentException e) {
            // Handle the specific error message returned from the service layer
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the short URL.");
        }
    }



    private ShortURLDto convertToDto(ShortURL shortURL) {
        ShortURLDto dto = new ShortURLDto();
        dto.setOriginalUrl(shortURL.getOriginalUrl());
        dto.setShortLink(shortURL.getShortLink());
        dto.setCreationDate(shortURL.getCreationDate().toString());
        dto.setExpirationDate(shortURL.getExpirationDate().toString());
        dto.setActive(shortURL.isActive());
        dto.setUserId(shortURL.getUserId());
        dto.setClickCount(shortURL.getClickCount());

        return dto;
    }
}
