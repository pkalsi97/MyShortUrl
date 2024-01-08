package com.pk.MyShortUrl.controller;

import com.pk.MyShortUrl.config.AppConfig;
import com.pk.MyShortUrl.model.ShortURL;
import com.pk.MyShortUrl.service.ShortURLService;
import com.pk.MyShortUrl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
public class ActiveUrlController {

    private final ShortURLService shortURLService;
    private final UserService userService;
    private final AppConfig appConfig;

    @Autowired
    public ActiveUrlController(ShortURLService shortURLService, UserService userService, AppConfig appConfig) {
        this.shortURLService = shortURLService;
        this.userService = userService;
        this.appConfig = appConfig;
    }

    @GetMapping("/active-urls")
    public ModelAndView viewActiveUrls(Principal principal) {
        String username = principal.getName();
        List<ShortURL> activeUrls = shortURLService.getActiveShortURLsByUser(username);
        ModelAndView modelAndView = new ModelAndView("active-urls");
        modelAndView.addObject("activeUrls", activeUrls);
        modelAndView.addObject("baseUrl", appConfig.getBaseUrl());
        return modelAndView;
    }

    @PostMapping("/deactivate-url")
    public String deactivateUrl(@RequestParam String urlId, Principal principal) {
        String username = principal.getName();
        boolean isDeactivated = shortURLService.deactivateUrl(urlId, username);
        if (isDeactivated) {
            userService.decrementActiveURLCount(username);
        }
        return "redirect:/active-urls";
    }


    // for inactive urls dashboard
    @GetMapping("/inactive-urls")
    public ModelAndView viewInactiveUrls(Principal principal) {
        String username = principal.getName();
        List<ShortURL> inactiveUrls = shortURLService.getInactiveShortURLsByUser(username);
        ModelAndView modelAndView = new ModelAndView("inactive-urls");
        modelAndView.addObject("inactiveUrls", inactiveUrls);
        return modelAndView;
    }
}
