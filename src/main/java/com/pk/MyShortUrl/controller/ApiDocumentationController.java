package com.pk.MyShortUrl.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@RequestMapping("/api-docs")
public class ApiDocumentationController {

    @GetMapping
    public String getApiDocumentation(Model model, Principal principal) {
        if (principal == null) {

        }
        return "api-docs";
    }

}