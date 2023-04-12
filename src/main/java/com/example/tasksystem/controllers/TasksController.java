package com.example.tasksystem.controllers;

import com.example.tasksystem.models.Task;
import com.example.tasksystem.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TasksController {

    @GetMapping("/tasks")
    public String tasks(Model model) {
        model.addAttribute("tasks", new Task[]{new Task("t1", "t1d", new User())});
        return "tasks";
    }

    @GetMapping("/tasks/new")
    public String newTask() {
        return "tasks-new";
    }

    @GetMapping("/tasks/new/form")
    public String newTaskForm(@RequestParam String title, @RequestParam String description) {
        return "redirect:/tasks";
    }

}
