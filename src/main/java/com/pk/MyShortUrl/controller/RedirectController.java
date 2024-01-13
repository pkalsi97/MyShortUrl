package com.pk.MyShortUrl.controller;

import com.pk.MyShortUrl.model.ShortURL;
import com.pk.MyShortUrl.service.ShortURLService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Optional;


// This controller that handles HTTP requests for URL redirection.
@Controller
public class RedirectController {
    // Injecting the ShortURLService to interact with URL data.
    private final ShortURLService shortURLService;
    // Reading the base URL value from the application properties.
    @Value("${app.baseUrl}")
    private String baseUrl;

    // Constructor for dependency injection of ShortURLService.
    public RedirectController(ShortURLService shortURLService) {
        this.shortURLService = shortURLService;
    }

    // Mapping a GET request to a dynamic path variable {shortLink}.
    // This method handles redirection from a short URL to its original URL.
    @GetMapping("/{shortLink}")
    public ModelAndView redirectToOriginalUrl(@PathVariable String shortLink) {
        String fullShortLink = baseUrl +"/"+shortLink;
        Optional<ShortURL> shortURLOptional = shortURLService.getShortURLByShortLink(fullShortLink);

        if (shortURLOptional.isPresent()) {
            ShortURL shortURL = shortURLOptional.get();
            if (shortURL.isActive() && shortURL.getExpirationDate().isAfter(LocalDateTime.now())) {
                shortURLService.incrementClickCount(shortURL.getId());
                return new ModelAndView("redirect:" + shortURL.getOriginalUrl());
            }
        }

        ModelAndView modelAndView = new ModelAndView("expired-link");
        modelAndView.addObject("message", "This short URL has expired or is invalid.");
        return modelAndView;
    }

}
