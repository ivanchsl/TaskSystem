package com.example.tasksystem.controllers;

import com.example.tasksystem.config.Auth;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        if (Auth.getAuth() == null) {
            return "index";
        }
        return "index-logged";
    }

}
