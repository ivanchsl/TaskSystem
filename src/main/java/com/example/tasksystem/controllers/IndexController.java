package com.example.tasksystem.controllers;

import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class IndexController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String index(Principal principal) {
        if (principal == null) {
            return "index";
        }
        return "index-logged";
    }

}
