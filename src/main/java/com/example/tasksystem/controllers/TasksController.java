package com.example.tasksystem.controllers;

import com.example.tasksystem.config.Auth;
import com.example.tasksystem.models.Task;
import com.example.tasksystem.repositories.TaskRepository;
import com.example.tasksystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TasksController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/tasks")
    public String tasks(Model model) {
        if (Auth.getAuth() == null) {
            return "redirect:/";
        }
        model.addAttribute("tasks", userRepository.findById(Auth.getAuth().getId()).get().getTasks());
        return "tasks";
    }

    @GetMapping("/tasks/new")
    public String newTask() {
        return "tasks-new";
    }

    @GetMapping("/tasks/new/form")
    public String newTaskForm(@RequestParam String title, @RequestParam String description) {
        if (Auth.getAuth() == null) {
            return "redirect:/";
        }
        taskRepository.save(new Task(title, description, Auth.getAuth()));
        return "redirect:/tasks";
    }

}
