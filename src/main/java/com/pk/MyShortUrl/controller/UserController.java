package com.pk.MyShortUrl.controller;

import com.pk.MyShortUrl.model.User;
import com.pk.MyShortUrl.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public ModelAndView showRegistrationForm() {
        ModelAndView modelAndView = new ModelAndView("register");
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        User newUser = userService.registerUser(username, email, password);
        if (newUser != null) {
            ModelAndView modelAndView = new ModelAndView("redirect:/loginPage");
            // Redirect to log in after successful registration
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("error", "Username already exists. Please try a different one.");
            return modelAndView;
        }
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return new ModelAndView("redirect:/loginPage");
    }

    @PostMapping("/login")
    public ModelAndView loginUser(@RequestParam String username, @RequestParam String password) {
        if (userService.loginUser(username, password)) {
            ModelAndView modelAndView = new ModelAndView("dashboard");
            modelAndView.addObject("username", username);
            User user = userService.getUserByUsername(username);
            modelAndView.addObject("urlLimit", user.getUrlLimit());
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("index");
            modelAndView.addObject("error", "Invalid username or password.");
            return modelAndView;
        }
    }

    @GetMapping("/loginPage")
    public String showLoginPage() {
        return "index"; // Assuming "login" is the name of your login HTML template
    }
}
