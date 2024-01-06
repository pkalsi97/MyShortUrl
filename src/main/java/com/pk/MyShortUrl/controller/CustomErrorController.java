package com.pk.MyShortUrl.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Return the name of the HTML template for your error page
        return "error"; // Assuming you have an "error.html" template
    }
}
