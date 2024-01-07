package com.pk.MyShortUrl.controller;

import com.pk.MyShortUrl.config.AppConfig;
import com.pk.MyShortUrl.model.ShortURL;
import com.pk.MyShortUrl.model.User;
import com.pk.MyShortUrl.service.ShortURLService;
import com.pk.MyShortUrl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class URLShorteningController {

    private final ShortURLService shortURLService;
    private final UserService userService;
    private final AppConfig appConfig;

    @Autowired
    public URLShorteningController(ShortURLService shortURLService, UserService userService, AppConfig appConfig) {
        this.shortURLService = shortURLService;
        this.userService = userService;
        this.appConfig = appConfig;
    }

    @GetMapping("/dashboard")
    public ModelAndView showDashboard(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("dashboard");
        String username = principal.getName();
        modelAndView.addObject("username", username);
        modelAndView.addObject("baseUrl", appConfig.getBaseUrl());
        modelAndView.addObject("totalAssignedUrl", userService.getUrlLimit(username));
        modelAndView.addObject("totalActiveUrl", userService.getActiveURLCount(username));
        return modelAndView;
    }

    @PostMapping("/create-short-url")
    @ResponseBody
    public ResponseEntity<?> createShortUrl(@RequestParam String longUrl, @RequestParam String backHalf, Principal principal) {
        String username = principal.getName();
        if (userService.getActiveURLCount(username) >= userService.getUrlLimit(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "URL limit reached."));
        }

        if (!shortURLService.isBackHalfUnique(backHalf) || !shortURLService.isBackHalfValid(backHalf)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Back-half is not unique. Please try a different one."));
        }

        try {
            ShortURL shortURL = shortURLService.createShortURL(longUrl, backHalf, username);
            if (shortURL != null) {
                userService.incrementActiveURLCount(username);
                return ResponseEntity.ok(Map.of("shortLink", shortURL.getShortLink(), "qrCode", shortURL.getQrCode()));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Error creating short URL."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "An unexpected error occurred."));
        }
    }
}
