package com.pk.MyShortUrl.controller;

import com.pk.MyShortUrl.model.ShortURL;
import com.pk.MyShortUrl.service.ShortURLService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class RedirectController {

    private final ShortURLService shortURLService;

    @Value("${app.baseUrl}")
    private String baseUrl;

    public RedirectController(ShortURLService shortURLService) {
        this.shortURLService = shortURLService;
    }

    @GetMapping("/{shortLink}")
    public ModelAndView redirectToOriginalUrl(@PathVariable String shortLink) {
        String fullShortLink = baseUrl + shortLink;
        Optional<ShortURL> shortURLOptional = shortURLService.getShortURLByShortLink(fullShortLink);

        if (shortURLOptional.isPresent()) {
            ShortURL shortURL = shortURLOptional.get();
            if (shortURL.isActive() && shortURL.getExpirationDate().isAfter(LocalDateTime.now())) {
                return new ModelAndView("redirect:" + shortURL.getOriginalUrl());
            }
        }

        ModelAndView modelAndView = new ModelAndView("expired-link");
        modelAndView.addObject("message", "This short URL has expired or is invalid.");
        return modelAndView;
    }
}
