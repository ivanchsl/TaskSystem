package com.example.tasksystem.controllers;

import com.example.tasksystem.models.Task;
import com.example.tasksystem.models.User;
import com.example.tasksystem.repositories.TaskRepository;
import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class TasksController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/tasks")
    public String tasks(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/";
        }
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("tasks", user.getTasks());
        return "tasks";
    }

    @GetMapping("/tasks/new")
    public String newTask(Principal principal) {
        if (principal == null) {
            return "redirect:/";
        }
        return "tasks-new";
    }

    @GetMapping("/tasks/new/form")
    public String newTaskForm(Principal principal, @RequestParam String title, @RequestParam String description) {
        if (principal == null) {
            return "redirect:/";
        }
        User user = userRepository.findByUsername(principal.getName());
        taskRepository.save(new Task(title, description, user));
        return "redirect:/tasks";
    }

}
