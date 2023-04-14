package com.example.tasksystem.controllers;

import com.example.tasksystem.config.Auth;
import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login/form")
    public String loginForm(@RequestParam String username, @RequestParam String password) {
        for (User user : userRepository.findAll()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                Auth.login(user);
            }
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout() {
        Auth.logout();
        return "redirect:/";
    }

}
