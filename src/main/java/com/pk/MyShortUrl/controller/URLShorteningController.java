package com.pk.MyShortUrl.controller;


import com.pk.MyShortUrl.config.AppConfig;
import com.pk.MyShortUrl.model.ShortURL;
import com.pk.MyShortUrl.service.ShortURLService;
import com.pk.MyShortUrl.service.UserService;
import com.pk.MyShortUrl.service.webriskSearchUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@Controller
public class URLShorteningController {

    private final ShortURLService shortURLService;
    private final UserService userService;
    private final AppConfig appConfig;

    private final webriskSearchUri webRiskSearchUri;

    @Autowired
    public URLShorteningController(ShortURLService shortURLService, UserService userService, AppConfig appConfig, webriskSearchUri webRiskSearchUri) {
        this.shortURLService = shortURLService;
        this.userService = userService;
        this.appConfig = appConfig;
        this.webRiskSearchUri = webRiskSearchUri;
    }

    // Handles creation of short urls, return a response entity
    @PostMapping("/create-short-url")
    @ResponseBody
    public ResponseEntity<?> createShortUrl(@RequestParam String longUrl, @RequestParam String backHalf, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
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

    // redirects in case incorrect access.
    @GetMapping("/create-short-url")
    public String handleIncorrectAccessToCreateShortUrl() {
        return "redirect:/error";
    }


    /// Used to validate the back half
    @GetMapping("/validate-backhalf")
    @ResponseBody
    public ResponseEntity<?> validateBackHalf(@RequestParam String backHalf, Principal principal) {
        if (principal == null && backHalf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        boolean isValid = shortURLService.isBackHalfValid(backHalf);
        boolean isAvailable = shortURLService.isBackHalfUnique(backHalf);
        return ResponseEntity.ok(Map.of("isValid", isValid, "isAvailable", isAvailable));
    }

    //used to generate random back half
    @GetMapping("/generate-backhalf")
    @ResponseBody
    public ResponseEntity<?> generateBackHalf(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        String backHalf = shortURLService.generateUniqueBackHalf();
        return ResponseEntity.ok(Map.of("backHalf", backHalf));
    }

    // check the validity of the original url using google webrisk API
    @GetMapping("/validate-original-url")
    public ResponseEntity<?> validateOriginalUrl(@RequestParam String url, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
        if (url == null || url.isEmpty()) {
            return ResponseEntity.badRequest().body("URL parameter is required");
        }

        boolean isSafe = webriskSearchUri.searchUri(url);
        return ResponseEntity.ok(Map.of("isValid", isSafe));
    }
}
