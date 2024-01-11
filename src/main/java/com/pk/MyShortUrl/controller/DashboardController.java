package com.pk.MyShortUrl.controller;

import com.pk.MyShortUrl.config.AppConfig;
import com.pk.MyShortUrl.model.ShortURL;
import com.pk.MyShortUrl.service.ShortURLService;
import com.pk.MyShortUrl.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {

    private final ShortURLService shortURLService;
    private final UserService userService;
    private final AppConfig appConfig;

    @Autowired
    public DashboardController(ShortURLService shortURLService, UserService userService, AppConfig appConfig) {
        this.shortURLService = shortURLService;
        this.userService = userService;
        this.appConfig = appConfig;
    }

    @GetMapping("/dashboard")
    public ModelAndView showDashboard(Principal principal) {
        if (principal == null) {
            return new ModelAndView("redirect:/error");
        }
        ModelAndView modelAndView = new ModelAndView("dashboard");
        String username = principal.getName();
        modelAndView.addObject("username", username);
        modelAndView.addObject("baseUrl", appConfig.getBaseUrl());
        modelAndView.addObject("totalAssignedUrl", userService.getUrlLimit(username));
        modelAndView.addObject("totalActiveUrl", userService.getActiveURLCount(username));
        return modelAndView;
    }

    @GetMapping("/api-docs")
    public ModelAndView apiDocumentation(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("api-docs");
        modelAndView.addObject("baseUrl", appConfig.getBaseUrl());
        if (principal == null) {
            return new ModelAndView("redirect:/error");
        }
        return modelAndView;
    }

    @GetMapping("/api-docs-all")
    public ModelAndView showApiDocs(HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("api-docs-all");
        modelAndView.addObject("baseUrl", appConfig.getBaseUrl());
        clearResponseCache(response);
        return modelAndView;
    }

    @GetMapping("/active-urls")
    public ModelAndView viewActiveUrls(Principal principal) {
        if (principal == null) {
            return new ModelAndView("redirect:/error");
        }
        String username = principal.getName();
        List<ShortURL> activeUrls = shortURLService.getActiveShortURLsByUser(username);
        ModelAndView modelAndView = new ModelAndView("active-urls");
        modelAndView.addObject("activeUrls", activeUrls);
        modelAndView.addObject("baseUrl", appConfig.getBaseUrl());
        return modelAndView;
    }

    @GetMapping("/inactive-urls")
    public ModelAndView viewInactiveUrls(Principal principal) {
        if (principal == null) {
            return new ModelAndView("redirect:/error");
        }
        String username = principal.getName();
        List<ShortURL> inactiveUrls = shortURLService.getInactiveShortURLsByUser(username);
        ModelAndView modelAndView = new ModelAndView("inactive-urls");
        modelAndView.addObject("inactiveUrls", inactiveUrls);
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

    private void clearResponseCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
