package com.example.tasksystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @GetMapping("/reg")
    public String reg() {
        return "reg";
    }

    @GetMapping("/reg/form")
    public String regForm(@RequestParam String username, @RequestParam String password) {
        return "redirect:/";
    }

}
