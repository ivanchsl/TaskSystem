package com.example.tasksystem.controllers;

import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/reg")
    public String reg() {
        return "reg";
    }

    @GetMapping("/reg/form")
    public String regForm(@RequestParam String username, @RequestParam String password) {
        userRepository.save(new User(username, password));
        return "redirect:/";
    }

}
